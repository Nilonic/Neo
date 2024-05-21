package nilon.neo.Lua;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nilon.neo.Lua.luaFunctions.SendMessage;
import nilon.neo.Neo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class LuaIntegration {
    private final Globals globals;
    private static final Logger lualog = LogManager.getLogger("Neo/Lua");

    public LuaIntegration() {
        lualog.info("Initializing globals...");
        globals = JsePlatform.standardGlobals();

        lualog.info("Initializing Neo-specific globals...");
        globals.set("sendChatMessage", new SendMessage());

        globals.set("Minecraft", CoerceJavaToLua.coerce(MinecraftClient.getInstance()));
        globals.set("McText", CoerceJavaToLua.coerce(Text.class));
        globals.set("Screens", CoerceJavaToLua.coerce(Screen.class));
    }

    public void printVer(CommandContext<FabricClientCommandSource> ctx) {
        sendFeedback(ctx, "LuaJ V3.0.1");
        sendFeedback(ctx, "Neo Lua globals V1.0.0");
    }

    public void runScript(String name, CommandContext<FabricClientCommandSource> ctx) {
        if (ctx == null || ctx.getSource() == null) {
            logAndSendError("CommandContext or CommandSource is null", ctx);
            return;
        }

        File scriptFile = new File(Neo.luaScriptsDir, name + ".lua");
        if (!isFileValid(scriptFile, name, ctx)) {
            return;
        }

        try (Reader reader = new FileReader(scriptFile)) {
            LuaValue chunk = globals.load(reader, scriptFile.getName());
            chunk.call();
            lualog.info("Executed script \"{}\".lua successfully", name);
            sendFeedback(ctx, "Lua return OK");
        } catch (IOException e) {
            logAndSendError("Error executing script \"" + name + ".lua\": " + e.getMessage(), ctx);
        } catch (LuaError e){
            String errorMsg = String.format("Lua error executing script \"%s.lua\": %s",
                    name, e.getMessage());
            logAndSendError(errorMsg, ctx);
        }
        catch (Exception e) {
            logAndSendError("Unexpected error executing script \"" + name + ".lua\": " + e.getMessage(), ctx);
        }
    }

    private boolean isFileValid(File scriptFile, String name, CommandContext<FabricClientCommandSource> ctx) {
        if (!scriptFile.exists()) {
            logAndSendError("Script \"" + name + ".lua\" not found", ctx);
            return false;
        }
        if (!scriptFile.canRead()) {
            logAndSendError("Cannot read script \"" + name + ".lua\" due to permission issues", ctx);
            return false;
        }
        return true;
    }

    private void logAndSendError(String message, CommandContext<FabricClientCommandSource> ctx) {
        lualog.error(message);
        sendError(ctx, message);
    }

    private void sendFeedback(CommandContext<FabricClientCommandSource> ctx, String message) {
        ctx.getSource().sendFeedback(Text.literal(message));
    }

    private void sendError(CommandContext<FabricClientCommandSource> ctx, String message) {
        ctx.getSource().sendError(Text.literal(message));
    }
}
