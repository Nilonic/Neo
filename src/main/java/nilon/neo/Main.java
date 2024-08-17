package nilon.neo;

import org.spongepowered.asm.mixin.Final;

import javax.swing.*;

public class Main {
    /**
     * Just in case someone launches this accidentally
     */
    @Final
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "ERROR: this is a Fabric mod, and cannot be run on it's own. make sure you have Fabric and Fabric API installed as well!\n-nilon", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}
