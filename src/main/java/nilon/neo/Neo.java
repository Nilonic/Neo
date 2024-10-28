package nilon.neo;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import nilon.neo.Lua.LuaIntegration;
import nilon.neo.exceptions.NeoInitializationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Neo implements ModInitializer {
    public static final String NEO_VER = "1.0";
    private static final Logger Log = LogManager.getLogger("Neo");
    static double tps;

    // TPS stuff
    private static final int TICK_INTERVAL = 20; // Interval for calculating TPS (in ticks)
    private long lastTime;
    private int tickCount;
    private int errorCount = 0;

    public static final boolean _DEBUG = true;

    public static File luaScriptsDir;

    @Override
    public void onInitialize() throws NeoInitializationError {
        Log.info("Initializing Neo V" + NEO_VER +", please wait...");
        try {
            Log.info("Initializing Lua Integration...");
            LuaIntegration luaIntegration = new LuaIntegration();

            Log.info("Creating necessary directories...");
            createDirectories();

            Log.info("Initializing Clientside commands...");
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("LuaVer")
                    .executes(context -> {
                        luaIntegration.printVer(context);
                        return 0;
                    })));

            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("RunLuaScript")
                    .then(ClientCommandManager.argument("script name", StringArgumentType.greedyString())
                            .executes(context -> {
                                String name = StringArgumentType.getString(context, "script name");
                                luaIntegration.runScript(name, context);
                                return 1;
                            }))));

            Log.info("Initializing Hud...");
            HudRenderCallback.EVENT.register(HUD::renderHud);

            Log.info("Initializing Tps Calculator...");
            ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
            lastTime = System.currentTimeMillis();
            tickCount = 0;
        }
        catch (Exception e){
            errorCount += 1;
        }
        if (errorCount < 1)
            Log.info("Initialized Neo successfully");
        else {
            Log.error("Neo encountered an error initializing");
            throw new NeoInitializationError("Neo encountered an error initializing");
        }
    }

    private void createDirectories() {
        luaScriptsDir = new File(MinecraftClient.getInstance().runDirectory, "Neo/LuaScripts");
        if (!luaScriptsDir.exists()) {
            boolean dirsCreated = luaScriptsDir.mkdirs();
            if (dirsCreated) {
                Log.info("Successfully created directory: {}", luaScriptsDir.getPath());
            } else {
                Log.error("Failed to create directory: {}", luaScriptsDir.getPath());
            }
        } else {
            Log.info("Directory already exists: {}", luaScriptsDir.getPath());
        }
    }

    private void onEndClientTick(MinecraftClient client) {
        updateTps();
    }

    private void updateTps() {
        tickCount++;

        if (tickCount >= TICK_INTERVAL) {
            long currentTime = System.currentTimeMillis();
            long timeDiff = currentTime - lastTime;

            tps = (tickCount / (timeDiff / 1000.0));
            // Log.info("Current TPS: {}", tps); // Uncomment this line to log TPS

            lastTime = currentTime;
            tickCount = 0;
        }
    }
}