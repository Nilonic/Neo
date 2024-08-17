package nilon.neo.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImportPathScreen extends Screen {
    private static final int TEXT_FIELD_WIDTH = 200;
    private static final int TEXT_FIELD_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 200;
    private static final String LUA_EXTENSION = ".lua";

    private final Screen parent;
    private TextFieldWidget input;

    public ImportPathScreen(Screen parentScreen) {
        super(Text.literal("Enter FULL PATH to .lua file"));
        this.parent = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        addTitle();
        addInputField();
        addImportButton();
        addCancelButton();
    }

    private void addTitle() {
        MultilineTextWidget title = new MultilineTextWidget(width / 2 - TEXT_FIELD_WIDTH / 2, 10,
                Text.translatable("neo.gui.import.title"), textRenderer);
        this.addDrawableChild(title);
    }

    private void addInputField() {
        input = new TextFieldWidget(textRenderer, width / 2 - TEXT_FIELD_WIDTH / 2, 30,
                TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Text.translatable("neo.gui.import.title"));
        input.setMaxLength(4096);
        this.addDrawableChild(input);
    }

    private void addImportButton() {
        ButtonWidget importButton = ButtonWidget.builder(Text.translatable("neo.gui.script.button"),
                button -> handleImport()).build();
        importButton.setPosition(width / 2 - BUTTON_WIDTH / 2, 60);
        this.addDrawableChild(importButton);
    }

    private void addCancelButton() {
        ButtonWidget cancelButton = ButtonWidget.builder(Text.translatable("gui.cancel"),
                button -> {
                    assert client != null;
                    client.setScreen(parent);
                }).build();
        cancelButton.setPosition(width / 2 - BUTTON_WIDTH / 2, 90);
        this.addDrawableChild(cancelButton);
    }

    private void handleImport() {
        // Get and sanitize the input path
        String sanitizedPath = sanitizeInput(input.getText());
        File file = new File(sanitizedPath);

        if (isValidLuaFile(file)) {
            File destinationFile = getDestinationFile(file);
            if (destinationFile.exists()) {
                showImportResultScreen("§cFile already exists", "A file with this name already exists in the destination folder.", false);
            } else {
                copyFile(file, destinationFile);
            }
        } else {
            showImportResultScreen("§cInvalid File", "The provided file path is either invalid or the file is not a .lua file.", false);
        }
    }

    private boolean isValidLuaFile(File file) {
        return file.exists() && file.getName().endsWith(LUA_EXTENSION);
    }

    private File getDestinationFile(File file) {
        String endPath = MinecraftClient.getInstance().runDirectory + "/Neo/LuaScripts/" + file.getName();
        return new File(endPath);
    }

    private void copyFile(File source, File destination) {
        try {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            showImportResultScreen("§aSuccess", "The file was successfully imported.", true);
        } catch (IOException e) {
            e.printStackTrace(); //TODO: add more robust logging
            showImportResultScreen("§cError", "An error occurred while copying the file.", false);
        }
    }

    private String sanitizeInput(String input) {
        return input.replace("\"", "")
                .replace("'", "");
    }

    private void showImportResultScreen(String title, String message, boolean success) {
        assert client != null;
        client.setScreen(new ImportResultScreen(title, message));
    }
}
