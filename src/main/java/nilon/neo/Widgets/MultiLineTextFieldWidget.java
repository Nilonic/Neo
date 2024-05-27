package nilon.neo.Widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MultiLineTextFieldWidget extends TextFieldWidget {

    private final TextRenderer textRenderer;
    private final List<String> lines;
    private int scrollOffset;

    public MultiLineTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text luaScript) {
        super(textRenderer, x, y, width, height, luaScript);
        this.textRenderer = textRenderer;
        this.lines = new ArrayList<>();
        this.scrollOffset = 0;
    }

    public MultiLineTextFieldWidget(TextRenderer textRenderer, int width, int height, Text text) {
        super(textRenderer, width, height, text);
        this.textRenderer = textRenderer;
        this.lines = new ArrayList<>();
        this.scrollOffset = 0;
    }

    public MultiLineTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
        this.textRenderer = textRenderer;
        this.lines = new ArrayList<>();
        this.scrollOffset = 0;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        this.lines.clear();
        for (String line : text.split("\n")) {
            this.lines.add(line);
        }
    }

    @Override
    public String getText() {
        return String.join("\n", this.lines);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        // Add handling for Enter key for line breaks
        if (keyCode == 257) { // Enter key
            int cursorPosition = this.getCursor();
            int currentLineIndex = getLineIndexFromCursorPosition(cursorPosition);
            String currentLine = this.lines.get(currentLineIndex);

            int lineCursorPosition = getLineCursorPosition(cursorPosition, currentLineIndex);

            String beforeCursor = currentLine.substring(0, lineCursorPosition);
            String afterCursor = currentLine.substring(lineCursorPosition);

            this.lines.set(currentLineIndex, beforeCursor);
            this.lines.add(currentLineIndex + 1, afterCursor);

            this.setCursor(cursorPosition + 1, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (super.charTyped(chr, modifiers)) {
            return true;
        }

        // Add handling for normal character typing
        int cursorPosition = this.getCursor();
        int currentLineIndex = getLineIndexFromCursorPosition(cursorPosition);
        String currentLine = this.lines.get(currentLineIndex);

        int lineCursorPosition = getLineCursorPosition(cursorPosition, currentLineIndex);

        String beforeCursor = currentLine.substring(0, lineCursorPosition);
        String afterCursor = currentLine.substring(lineCursorPosition);

        this.lines.set(currentLineIndex, beforeCursor + chr + afterCursor);
        this.setCursor(cursorPosition + 1, false);

        return true;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int yStart = this.getY();
        int lineHeight = this.textRenderer.fontHeight;

        Matrix4f matrix = new Matrix4f(); // Create a new matrix

        for (int i = this.scrollOffset; i < this.lines.size() && (yStart + lineHeight) <= (this.getY() + this.height); i++) {
            matrix.identity(); // Reset matrix to identity before applying transformations
            matrix.translate((float)this.getX(), (float)yStart, 0); // Translate to widget position

            // Now use the custom matrix instead of context.getMatrices()
            VertexConsumerProvider vertexConsumers = context.getVertexConsumers();
            //this.textRenderer.draw(this.lines.get(i), matrix, vertexConsumers, 0xFFFFFF, false, 0x000000, 0);
            this.textRenderer.draw(this.lines.get(i), 0, 0, 0xFFFFFF, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0x000000, 0);

            yStart += lineHeight;
        }
    }



    // Add scrolling methods
    public void scroll(int amount) {
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset + amount, this.lines.size() - 1));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.scroll((int) amount);
        return true;
    }

    // Utility methods to calculate cursor position within the text
    private int getLineIndexFromCursorPosition(int cursorPosition) {
        int charsCount = 0;
        for (int i = 0; i < this.lines.size(); i++) {
            charsCount += this.lines.get(i).length() + 1; // Adding 1 for the newline character
            if (charsCount > cursorPosition) {
                return i;
            }
        }
        return this.lines.size() - 1; // Default to last line
    }

    private int getLineCursorPosition(int cursorPosition, int lineIndex) {
        int charsCount = 0;
        for (int i = 0; i < lineIndex; i++) {
            charsCount += this.lines.get(i).length() + 1; // Adding 1 for the newline character
        }
        return cursorPosition - charsCount;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        super.appendClickableNarrations(builder);
    }

    // Override other necessary methods as required, such as rendering, handling text input, etc.

    @Override
    public void setChangedListener(Consumer<String> changedListener) {
        super.setChangedListener(changedListener);
    }

    @Override
    public void setRenderTextProvider(BiFunction<String, Integer, OrderedText> renderTextProvider) {
        super.setRenderTextProvider(renderTextProvider);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return super.getNarrationMessage();
    }

    @Override
    public String getSelectedText() {
        return super.getSelectedText();
    }

    @Override
    public void setTextPredicate(Predicate<String> textPredicate) {
        super.setTextPredicate(textPredicate);
    }

    @Override
    public void write(String text) {
        super.write(text);
    }

    @Override
    public void eraseWords(int wordOffset) {
        super.eraseWords(wordOffset);
    }

    @Override
    public void eraseCharacters(int characterOffset) {
        super.eraseCharacters(characterOffset);
    }

    @Override
    public void eraseCharactersTo(int position) {
        super.eraseCharactersTo(position);
    }

    @Override
    public int getWordSkipPosition(int wordOffset) {
        return super.getWordSkipPosition(wordOffset);
    }

    @Override
    public void moveCursor(int offset, boolean shiftKeyPressed) {
        super.moveCursor(offset, shiftKeyPressed);
    }

    @Override
    public void setCursor(int cursor, boolean shiftKeyPressed) {
        super.setCursor(cursor, shiftKeyPressed);
    }

    @Override
    public void setSelectionStart(int cursor) {
        super.setSelectionStart(cursor);
    }

    @Override
    public void setCursorToStart(boolean shiftKeyPressed) {
        super.setCursorToStart(shiftKeyPressed);
    }

    @Override
    public void setCursorToEnd(boolean shiftKeyPressed) {
        super.setCursorToEnd(shiftKeyPressed);
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        super.playDownSound(soundManager);
    }

    @Override
    public void setMaxLength(int maxLength) {
        super.setMaxLength(maxLength);
    }

    @Override
    public int getCursor() {
        return super.getCursor();
    }

    @Override
    public boolean drawsBackground() {
        return super.drawsBackground();
    }

    @Override
    public void setDrawsBackground(boolean drawsBackground) {
        super.setDrawsBackground(drawsBackground);
    }

    @Override
    public void setEditableColor(int editableColor) {
        super.setEditableColor(editableColor);
    }

    @Override
    public void setUneditableColor(int uneditableColor) {
        super.setUneditableColor(uneditableColor);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
    }

    @Override
    public int getInnerWidth() {
        return super.getInnerWidth();
    }

    @Override
    public void setSelectionEnd(int index) {
        super.setSelectionEnd(index);
    }

    @Override
    public void setFocusUnlocked(boolean focusUnlocked) {
        super.setFocusUnlocked(focusUnlocked);
    }

    @Override
    public boolean isVisible() {
        return super.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @Override
    public void setSuggestion(@Nullable String suggestion) {
        super.setSuggestion(suggestion);
    }

    @Override
    public int getCharacterX(int index) {
        return super.getCharacterX(index);
    }
}
