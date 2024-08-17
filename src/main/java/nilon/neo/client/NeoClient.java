package nilon.neo.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import nilon.neo.Neo;
import nilon.neo.modules.Module;
import nilon.neo.modules.clientModules.AutoSprint;
import nilon.neo.modules.clientModules.Dummy;
import nilon.neo.modules.clientModules.KickSelf;
import nilon.neo.modules.clientModules.Rainbow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CopyOnWriteArrayList;

public class NeoClient implements ClientModInitializer {

    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<KeyBinding> keys = new CopyOnWriteArrayList<>();
    public static boolean isRainbowGui = false;
    public static boolean showGUI = true;
    private static final Logger clientLogger = LogManager.getLogger("Neo/Client");

    public static String lastIP;
    public static boolean lastIpLocal;

    @Override
    public void onInitializeClient() {
        Module[] modulesToInitialize;
        if (Neo._DEBUG) {
            modulesToInitialize = new Module[]{new Dummy(), new Rainbow(), new AutoSprint(), new KickSelf()};
        }
        else{
            modulesToInitialize = new Module[] {new Rainbow(), new AutoSprint(), new KickSelf()};
        }
        int doneModules = 1;

        for (Module module : modulesToInitialize) {
            clientLogger.info("Initializing module {} ({} out of {} initialized)", module.name, doneModules, modulesToInitialize.length);
            modules.add(module);
            keys.add(setupKeyCallback(module));
            doneModules += 1;
        }
        clientLogger.info("Binding Keys to END_CLIENT_TICK");
        for (KeyBinding key : keys){
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (key.wasPressed()) {
                    for (Module module : modules){
                        // allows for keybinding reassignment
                        if (key.getDefaultKey().getCode() == module.getKey()){
                            module.toggle();
                        }
                    }
                }
            });
        }

        ClientTickEvents.END_WORLD_TICK.register(client -> {
            for (Module module : modules) {
                module.onTick();
            }
        });
        clientLogger.info("Completed module initialization");
    }

    private KeyBinding setupKeyCallback(Module module) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "neo.modules." + module.name,
                InputUtil.Type.KEYSYM,
                module.getKey(),
                "category.neo.modules"
        ));
    }

}
