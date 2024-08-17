package nilon.neo.modules.clientModules;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class AutoSprint extends nilon.neo.modules.Module {
    private static MinecraftClient client;
    public AutoSprint() {
        super("Auto_Sprint", GLFW.GLFW_KEY_V, Category.MOVEMENT);
        client = MinecraftClient.getInstance();
    }

    @Override
    public void onTick() {
        if (this.toggled) {
            assert client.player != null;
            if (client.player.forwardSpeed > 0) {
                if (!client.player.isSprinting()) {
                    client.player.setSprinting(true);
                }
            }
        }
    }
}
