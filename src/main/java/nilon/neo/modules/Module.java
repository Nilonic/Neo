package nilon.neo.modules;

import net.minecraft.client.MinecraftClient;

import java.awt.*;

public abstract class Module {

    public String name;
    public boolean toggled;
    public int keyCode;
    public Category category;
    public MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, int key, Category category) {
        this.name = name;
        this.keyCode = key;
        this.category = category;
    }

    public boolean isEnabled() {
        return toggled;
    }

    public int getKey() {
        return keyCode;
    }

    public void onEvent(Event e) {

    }

    public abstract void onEvent(nilon.neo.events.Event e);

    public void toggle() {
        toggled = !toggled;
        if (toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {
        //System.out.println(name + " enabled");
    }

    public void onDisable() {
        //System.out.println(name + " disabled");
    }

    public enum Category {
        COMBAT,
        MOVEMENT,
        PLAYER,
        RENDER,
        CLIENT;
    }

    public enum CategoryColor {
        COMBAT(Color.RED),
        MOVEMENT(Color.BLUE),
        PLAYER(Color.YELLOW),
        RENDER(Color.GREEN),
        CLIENT(new Color(128, 0, 128)); // Purple color

        private final Color color;

        CategoryColor(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }
}
