package nilon.neo.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import nilon.neo.modules.Module;
import nilon.neo.modules.clientModules.Dummy;
import nilon.neo.modules.clientModules.Rainbow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CopyOnWriteArrayList;

public class NeoClient implements ClientModInitializer {

    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<KeyBinding> keys = new CopyOnWriteArrayList<>();
    public static boolean isRainbowGui = false;
    public static boolean showGUI = true;
    private static MinecraftClient client;
    private static final Logger clientLogger = LogManager.getLogger("Neo/Client");

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        Module[] modulesToInitialize = {new Dummy(), new Rainbow()};
        int doneModules = 0;

        for (Module module : modulesToInitialize) {
            clientLogger.info("Initializing module {} ({} out of {} initialized)", module.name, doneModules, modulesToInitialize.length - 1);
            modules.add(module);
            keys.add(setupKeyCallback(module));
            doneModules += 1;
        }
        clientLogger.info("Binding Keys to END_CLIENT_TICK");
        for (KeyBinding key : keys){
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (key.wasPressed()) {
                    for (Module module : modules){
                        if (key.matchesKey(module.keyCode, module.keyCode)){
                            module.toggle();
                        }
                    }
                }
            });
        }
        clientLogger.info("Completed module initialization");
    }

    private KeyBinding setupKeyCallback(Module module) {
        var x = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                module.name,
                InputUtil.Type.KEYSYM,
                module.getKey(),
                "category.neo.modules"
        ));
        return x;
    }

    private void handleKeyPress(int key) {
        for (Module module : modules) {
            if (module.getKey() == key) {
                module.toggle(); // Toggle the module when its key is pressed
                System.out.println(module.name + " toggled to " + (module.isEnabled() ? "enabled" : "disabled"));
            }
        }
    }
}
