package nilon.neo.modules.clientModules;

import nilon.neo.client.NeoClient;
import nilon.neo.modules.Module;
import org.lwjgl.glfw.GLFW;

public class Freecam extends Module {

    public Freecam() {
        super("Freecam", GLFW.GLFW_KEY_J, Category.PLAYER);
    }

    @Override
    public void onEnable() {
        NeoClient.isFreecam = true;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        NeoClient.isFreecam = false;
        super.onDisable();
    }

    @Override
    public void onTick() {
        super.onTick();
    }
}
