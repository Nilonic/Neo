package nilon.neo.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import nilon.neo.client.NeoClient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectMenuMixin extends Screen {

    @Shadow @Final private DirectionalLayoutWidget grid;

    @Unique private ButtonWidget button;

    protected DisconnectMenuMixin() {
        super(Text.literal("Reconnecting..."));
    }

    @Inject(at= @At("HEAD"), method = "init")
    private void init(CallbackInfo ci){
        if (!NeoClient.lastIpLocal) {
            button = (ButtonWidget.builder(Text.literal("Reconnect"), (button -> {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                ConnectScreen.connect(new TitleScreen(), minecraftClient, ServerAddress.parse(NeoClient.lastIP), new ServerInfo("idk", NeoClient.lastIP, ServerInfo.ServerType.OTHER), false, null);
            })).build());
        }
    }

    /**
     * @author Nilonic
     * @reason To prevent a crash
     */
    @Overwrite
    public void initTabNavigation(){
        if (!NeoClient.lastIpLocal) {
            this.grid.add(button);
            this.grid.refreshPositions();
            this.addDrawableChild(button);
        }
        SimplePositioningWidget.setPos(this.grid, this.getNavigationFocus());
    }
}
