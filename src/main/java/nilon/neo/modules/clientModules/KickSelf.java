package nilon.neo.modules.clientModules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import nilon.neo.client.NeoClient;
import nilon.neo.modules.Module;
import org.lwjgl.glfw.GLFW;

public class KickSelf extends Module {
    public KickSelf(){
        super("Kick_Self", GLFW.GLFW_KEY_KP_0, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        assert MinecraftClient.getInstance().player != null;
        String address = MinecraftClient.getInstance().player.networkHandler.getConnection().getAddressAsString(true);
        NeoClient.lastIpLocal = (address.contains("local"));
        int slashIndex = address.indexOf('/');
        if (slashIndex != -1) {
            NeoClient.lastIP = address.substring(slashIndex + 1);
        } else {
            NeoClient.lastIP = address;
        }
        System.out.println(NeoClient.lastIP);
        MinecraftClient.getInstance().player.networkHandler.getConnection().disconnect(Text.literal("You kicked yourself"));
        super.toggle(); // turns this off, so we don't kick ourselves again
    }
}
