package nilon.neo.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ImportResultScreen extends Screen {
    private final Text message;
    protected ImportResultScreen(String title, String msg){
        super(Text.literal(title));
        message = Text.literal(msg);
    }

    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
            assert this.client != null;
            this.client.setScreen((Screen)null);
        }).dimensions(this.width / 2 - 100, 140, 200, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 90, 16777215);
        context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, 110, 16777215);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}
