package nilon.neo.modules.clientModules;

import nilon.neo.modules.Module;
import org.lwjgl.glfw.GLFW;

public class PanicQuit extends Module {

    public PanicQuit() {
        super("Panic_Quit", GLFW.GLFW_KEY_K, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        System.exit(0);
    }
}
