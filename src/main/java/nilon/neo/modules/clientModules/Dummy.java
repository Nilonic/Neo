package nilon.neo.modules.clientModules;


import nilon.neo.events.Event;
import nilon.neo.modules.Module;
import org.lwjgl.glfw.GLFW;


public class Dummy extends Module {
    public Dummy() {
        super("Dummy", GLFW.GLFW_KEY_G, Category.CLIENT);
    }

    public Dummy(String name, int key, Category category) {
        super(name, key, category);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public int getKey() {
        return super.getKey();
    }

    @Override
    public void onEvent(Event e) {
    }

    @Override
    public void toggle() {
        super.toggle();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
