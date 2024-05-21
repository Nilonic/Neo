package nilon.neo.Lua.luaFunctions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class SendMessage extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue arg) {
        // Get the message from the Lua argument
        String message = arg.checkjstring();

        // Send the message to the Minecraft chat
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);

        return LuaValue.NIL;
    }
}