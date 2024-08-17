package nilon.neo.modules.clientModules;


import nilon.neo.modules.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Dummy module. not used for much other than confirming everything works
 */
public class Dummy extends Module {
    public Dummy() {
        super("Dummy", GLFW.GLFW_KEY_G, Category.CLIENT);
    }
}
