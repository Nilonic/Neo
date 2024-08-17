package nilon.neo.modules;

/**
 * Abstract class representing a module.
 * Modules are individual features or functionalities that can be toggled on or off.
 * Each module belongs to a specific category and can be associated with a keybinding.
 */
public abstract class Module {

    /** The name of the module */
    public String name;

    /** Indicates whether the module is currently toggled on */
    public boolean toggled;

    /** The key code associated with the module for toggling */
    public int keyCode;

    /** The category to which the module belongs */
    public Category category;

    /**
     * Constructs a new Module with the specified name, key code, and category.
     *
     * @param name the name of the module
     * @param key the key code to toggle the module
     * @param category the category of the module
     */
    public Module(String name, int key, Category category) {
        this.name = name;
        this.keyCode = key;
        this.category = category;
    }

    /**
     * Checks if the module is currently enabled.
     *
     *
     * @return true if the module is enabled, false otherwise
     */
    public boolean isEnabled() {
        return toggled;
    }

    /**
     * Retrieves the key code associated with the module.
     *
     * @return the key code of the module
     */
    public int getKey() {
        return keyCode;
    }

    /**
     * Called every tick while the module is enabled.
     * Subclasses should override this method to define behavior that should
     * occur on each game tick.
     */
    public void onTick() {
        // Override in subclasses for tick behavior
    }

    /**
     * Toggles the module on or off. When toggled on, the {@link #onEnable()} method is called.
     * When toggled off, the {@link #onDisable()} method is called.
     */
    public void toggle() {
        toggled = !toggled;
        if (toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /**
     * Called when the module is enabled. Subclasses should override this method to define
     * behavior that should occur when the module is enabled.
     */
    public void onEnable() {
        // Override in subclasses for enable behavior
    }

    /**
     * Called when the module is disabled. Subclasses should override this method to define
     * behavior that should occur when the module is disabled.
     */
    public void onDisable() {
        // Override in subclasses for disable behavior
    }

    /**
     * Enumeration representing the different categories of modules.
     * Modules are grouped into categories for organization and management.
     */
    public enum Category {
        /** Modules related to combat mechanics */
        COMBAT,

        /** Modules related to movement mechanics */
        MOVEMENT,

        /** Modules related to player interactions */
        PLAYER,

        /** Modules related to rendering and visual effects */
        RENDER,

        /** Client-side modules */
        CLIENT
    }
}
