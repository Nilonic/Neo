package nilon.neo.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ScrollableTextWidget;
import net.minecraft.text.Text;
import nilon.neo.Neo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NeoScriptScreen extends Screen {

    ScrollableTextWidget scriptList;

    public NeoScriptScreen() {
        super(Text.literal("Neo Scripts"));
    }

    @Override
    protected void init() {
        super.init();

        // Neo Config Title
        this.addDrawableChild(new MultilineTextWidget(width / 2 - 100, 10, Text.literal("Neo Config"), textRenderer));

        // Scrollable Box for Scripts

        StringBuilder scriptListStr = new StringBuilder();

        // List all Lua scripts
        List<File> luaScripts = listLuaScripts();
        //File runDirectory = MinecraftClient.getInstance().runDirectory;

        for (File script : luaScripts) {
            //String relativePath = runDirectory.toPath().relativize(script.toPath()).toString();
            scriptListStr.append("%s\n".formatted(script.getName()));
        }
        scriptList = new ScrollableTextWidget(width / 2 - 100, 40, 200, height - 80, Text.literal(scriptListStr.toString()), textRenderer);
        scriptList.setFocused(true);
        this.addDrawableChild(scriptList);

        // Buttons on the right
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonX = width / 2 + 120;
        int buttonY = height / 2 - 60;
        int buttonGap = 5;

        // Open config.json Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Open config.json"), (button) -> {
            // Your code to open config.json
        }).dimensions(buttonX, buttonY, buttonWidth, buttonHeight).build());
        buttonY += buttonHeight + buttonGap;

        // Exit Menu Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Exit Menu"), (button) -> {
            assert this.client != null;
            this.client.setScreen(null);
        }).dimensions(buttonX, buttonY, buttonWidth, buttonHeight).build());
        buttonY += buttonHeight + buttonGap;

        // Exit Game Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Exit Game"), (button) -> {
            assert this.client != null;
            this.client.stop();
        }).dimensions(buttonX, buttonY, buttonWidth, buttonHeight).build());
        buttonY += buttonHeight + buttonGap;

        // Debug Mode Toggle Button
        String debugText = Neo.isDebug() ? "??!" : "???";
        ButtonWidget debugButton = ButtonWidget.builder(Text.literal(debugText), (button) -> {
            Neo.toggleDebug();
            button.setMessage(Text.literal(Neo.isDebug() ? "??!" : "???"));
        }).dimensions(buttonX, buttonY, buttonWidth, buttonHeight).tooltip(Tooltip.of(Text.literal("ooh, secret"))).build();
        this.addDrawableChild(debugButton);
    }


    private @NotNull List<File> listLuaScripts() {
        List<File> luaScripts = new ArrayList<>();
        File luaScriptsDir = Neo.luaScriptsDir;
        if (luaScriptsDir.exists() && luaScriptsDir.isDirectory()) {
            File[] files = luaScriptsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".lua")) {
                        luaScripts.add(file);
                    }
                }
            }
        }
        return luaScripts;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        scriptList.setFocused(true);
    }
}