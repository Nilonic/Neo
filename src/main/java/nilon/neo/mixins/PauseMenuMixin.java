package nilon.neo.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import nilon.neo.screens.NeoScriptScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(GameMenuScreen.class)
public abstract class PauseMenuMixin extends Screen {

    @Shadow @Final private static Text RETURN_TO_MENU_TEXT;
    @Shadow protected abstract ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier);
    @Shadow @Final private static Text ADVANCEMENTS_TEXT;
    @Shadow @Final private static Text STATS_TEXT;
    @Shadow protected abstract ButtonWidget createUrlButton(Text text, String url);
    @Shadow @Final private static Text SEND_FEEDBACK_TEXT;
    @Shadow @Final private static Text REPORT_BUGS_TEXT;
    @Shadow @Final private static Text OPTIONS_TEXT;
    @Shadow @Final private static Text SHARE_TO_LAN_TEXT;
    @Shadow @Final private static Text PLAYER_REPORTING_TEXT;
    @Shadow @Nullable private ButtonWidget exitButton;
    @Shadow protected abstract void disconnect();
    @Mutable @Shadow @Final private boolean showMenu;
    @Shadow @Final private static Text GAME_TEXT;
    @Shadow @Final private static Text PAUSED_TEXT;

    public PauseMenuMixin(boolean showMenu) {
        super(showMenu ? GAME_TEXT : PAUSED_TEXT);
        this.showMenu = showMenu;
    }

    /**
     * @author Nilonic
     * @reason stuff
     */
    @Overwrite
    public void init() {
        if (this.showMenu) {
            cinitWidgets();
        }

        int yOffset = this.showMenu ? 40 : 10;
        int widgetWidth = this.width;
        Objects.requireNonNull(this.textRenderer);
        this.addDrawableChild(new TextWidget(0, yOffset, widgetWidth, 9, this.title, this.textRenderer));
    }

    @Unique
    private void cinitWidgets() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, 4, 4, 0);
        GridWidget.Adder adder = gridWidget.createAdder(2);
        adder.add(ButtonWidget.builder(RETURN_TO_MENU_TEXT, (button) -> {
            assert this.client != null;
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }).width(204).build(), 2, gridWidget.copyPositioner().marginTop(50));
        adder.add(this.createButton(ADVANCEMENTS_TEXT, () -> {
            assert this.client != null;
            assert this.client.player != null;
            return new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler(), this);
        }));
        adder.add(this.createButton(STATS_TEXT, () -> {
            assert this.client != null;
            assert this.client.player != null;
            return new StatsScreen(this, this.client.player.getStatHandler());
        }));
        adder.add(this.createUrlButton(SEND_FEEDBACK_TEXT, SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game"));
        adder.add(this.createUrlButton(REPORT_BUGS_TEXT, "https://aka.ms/snapshotbugs?ref=game")).active = !SharedConstants.getGameVersion().getSaveVersion().isNotMainSeries();
        adder.add(this.createButton(OPTIONS_TEXT, () -> {
            assert this.client != null;
            return new OptionsScreen(this, this.client.options);
        }));
        assert this.client != null;
        if (this.client.isIntegratedServerRunning() && !Objects.requireNonNull(this.client.getServer()).isRemote()) {
            adder.add(this.createButton(SHARE_TO_LAN_TEXT, () -> new OpenToLanScreen(this)));
        } else {
            adder.add(this.createButton(PLAYER_REPORTING_TEXT, () -> new SocialInteractionsScreen(this)));
        }

        adder.add(this.createButton(Text.literal("§dNeo Scripts"), NeoScriptScreen::new));

        Text text = this.client.isInSingleplayer() ? RETURN_TO_MENU_TEXT : ScreenTexts.DISCONNECT;
        this.exitButton = adder.add(ButtonWidget.builder(text, (button) -> {
            button.active = false;
            this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::disconnect, true);
        }).width(204).build(), 2);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height, 0.5F, 0.25F);
        gridWidget.forEachChild(this::addDrawableChild);
    }

}
