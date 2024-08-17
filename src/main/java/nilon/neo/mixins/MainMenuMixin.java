package nilon.neo.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import nilon.neo.Neo;
import nilon.neo.screens.NeoScriptScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Calendar;

@Mixin(TitleScreen.class)
public abstract class MainMenuMixin extends Screen {

    // shadows
    @Shadow @Nullable protected abstract Text getMultiplayerDisabledText();
    @Shadow @Nullable private SplashTextRenderer splashText;
    @Shadow private long backgroundFadeStart;
    @Shadow private boolean doBackgroundFade;
    @Shadow private float backgroundAlpha;
    @Shadow protected abstract void setWidgetAlpha(float alpha);
    @Shadow @Final private LogoDrawer logoDrawer;
    @Shadow protected abstract boolean isRealmsNotificationsGuiDisplayed();
    @Shadow @Nullable private RealmsNotificationsScreen realmsNotificationGui;


    protected MainMenuMixin(Text title) {
        super(title);
    }


    @Inject(at = @At("HEAD"), method = "init()V")
    protected void init(CallbackInfo ci){
        this.splashText = new SplashTextRenderer(getRandomSplash());
    }

    @Unique
    private static final String[] RAINBOW_COLORS = {
            "§c", // Red
            "§6", // Orange
            "§e", // Yellow
            "§a", // Green
            "§b", // Cyan
            "§9"  // Blue
    };

    @Unique
    private static String generateRainbowText(String text) {
        StringBuilder rainbowText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            String color = RAINBOW_COLORS[i % RAINBOW_COLORS.length];
            rainbowText.append(color).append(character);
        }
        return rainbowText.toString();
    }
    @Unique
    private String getRandomSplash() {
        String[] randomSplash = new String[]{"What the dog doin?", "the sky is the limit... sometimes", "Minecraft: now with 100% more bugs", "Minecraft: now with 100% more battery-draining stuff", "what did i do?", "We support pride!"};
        if (isJune()) {
            return generateRainbowText(randomSplash[Random.create().nextBetween(0, randomSplash.length - 1)]);
        } else {
            var res = randomSplash[Random.create().nextBetween(0, randomSplash.length - 1)];
            if (res.contains("We support pride")){
                return generateRainbowText(res);
            }
            else{
                return res;
            }
        }
    }

    @Unique
    private boolean isJune() {
        try {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            return month == Calendar.JUNE;
        } catch (Exception e) {
            return false; // in case something breaks
        }
    }

    /**
     * @author Nilonic
     * @reason just 2 add more buttons
     */
    @Overwrite
    private void initWidgetsNormal(int y, int spacingY) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§dNeo Scripts"), (button) -> {
            assert this.client != null;
            this.client.setScreen(new NeoScriptScreen());
        }).dimensions(this.width / 2 - 100, y - spacingY, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"), (button) -> {
            assert this.client != null;
            this.client.setScreen(new SelectWorldScreen(this));
        }).dimensions(this.width / 2 - 100, y, 200, 20).build());
        Text text = this.getMultiplayerDisabledText();
        boolean bl = text == null;
        Tooltip tooltip = text != null ? Tooltip.of(text) : null;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"), (button) -> {
            assert this.client != null;
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen(screen);
        }).dimensions(this.width / 2 - 100, y + spacingY, 200, 20).tooltip(tooltip).build()).active = bl;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.online"), (buttonWidget) -> {
            assert this.client != null;
            this.client.setScreen(new RealmsMainScreen(this));
        }).dimensions(this.width / 2 - 100, y + spacingY * 2, 200, 20).tooltip(tooltip).build()).active = bl;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        float f = 1.0F;
        if (this.doBackgroundFade) {
            float g = (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0F;
            if (g > 1.0F) {
                this.doBackgroundFade = false;
                this.backgroundAlpha = 1.0F;
            } else {
                g = MathHelper.clamp(g, 0.0F, 1.0F);
                f = MathHelper.clampedMap(g, 0.5F, 1.0F, 0.0F, 1.0F);
                this.backgroundAlpha = MathHelper.clampedMap(g, 0.0F, 0.5F, 0.0F, 1.0F);
            }

            this.setWidgetAlpha(f);
        }

        this.renderPanoramaBackground(context, delta);
        int i = MathHelper.ceil(f * 255.0F) << 24;
        if ((i & -67108864) != 0) {
            super.render(context, mouseX, mouseY, delta);
            this.logoDrawer.draw(context, this.width, f);

            if (this.splashText != null) {
                assert this.client != null;
                if (!(Boolean)this.client.options.getHideSplashTexts().getValue()) {
                    this.splashText.render(context, this.width, this.textRenderer, i);
                }
            }

            String string = "Minecraft " + SharedConstants.getGameVersion().getName();
            assert this.client != null;
            if (this.client.isDemo()) {
                string = string + " Demo";
            } else {
                string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
            }

            if (MinecraftClient.getModStatus().isModded()) {
                string = string + I18n.translate("menu.modded");
            }

            string = string + " (NEO V %s)".formatted(Neo.NEO_VER);

            context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 16777215 | i);
            if (this.isRealmsNotificationsGuiDisplayed() && f >= 1.0F) {
                RenderSystem.enableDepthTest();
                assert this.realmsNotificationGui != null;
                this.realmsNotificationGui.render(context, mouseX, mouseY, delta);
            }

        }
    }


}
