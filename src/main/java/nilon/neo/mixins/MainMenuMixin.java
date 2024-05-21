package nilon.neo.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MainMenuMixin extends Screen {

    @Shadow @Nullable protected abstract Text getMultiplayerDisabledText();

    @Shadow @Nullable private SplashTextRenderer splashText;

    protected MainMenuMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    protected void init(CallbackInfo ci){
        this.splashText = new SplashTextRenderer("Neo 4 Life");
    }

    /**
     * @author Nilonic
     * @reason ...
     */
    @Overwrite
    private void initWidgetsNormal(int y, int spacingY) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"), (button) -> {
            this.client.setScreen(new SelectWorldScreen(this));
        }).dimensions(this.width / 2 - 100, y, 200, 20).build());
        Text text = this.getMultiplayerDisabledText();
        boolean bl = text == null;
        Tooltip tooltip = text != null ? Tooltip.of(text) : null;
        ((ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"), (button) -> {
            assert this.client != null;
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen((Screen)screen);
        }).dimensions(this.width / 2 - 100, y + spacingY, 200, 20).tooltip(tooltip).build())).active = bl;
        ((ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.online"), (buttonWidget) -> {
            assert this.client != null;
            this.client.setScreen(new RealmsMainScreen(this));
        }).dimensions(this.width / 2 - 100, y + spacingY * 2, 200, 20).tooltip(tooltip).build())).active = bl;

    }

}
