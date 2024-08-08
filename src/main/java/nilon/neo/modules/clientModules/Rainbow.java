package nilon.neo.modules.clientModules;

import nilon.neo.client.NeoClient;
import nilon.neo.events.Event;
import nilon.neo.modules.Module;
import org.lwjgl.glfw.GLFW;

public class Rainbow extends Module {
    public Rainbow() {
        super("Rainbow GUI", GLFW.GLFW_KEY_Z, Category.CLIENT);
    }

    @Override
    public void onEvent(Event e) {

    }

    @Override
    public void toggle() {
        NeoClient.isRainbowGui = ! NeoClient.isRainbowGui;
        super.toggle();
    }
}
