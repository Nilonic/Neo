package nilon.neo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import nilon.neo.client.NeoClient;
import nilon.neo.modules.Module;
import org.joml.Matrix4f;

import java.awt.*;

public class HUD {
    private static float hue = 1.0f; // Hue value for rainbow effect
    private static Color color = Color.getHSBColor(hue, 1.0f, 1.0f);
    public static void renderHud(DrawContext drawContext, float tickDelta) {
        if (NeoClient.isRainbowGui) {
            // Calculate rainbow color only when rainbow effect is enabled
            hue += 0.001f; // Adjust speed of color change by changing increment value
            if (hue > 1) {
                hue -= 1; // Reset hue value to keep it within range
            }
            color = Color.getHSBColor(hue, 1.0f, 1.0f);
        } else {
            // Set hue to a constant value to keep color fixed
            hue = 1.0f; // Set hue to initial value
            color = Color.white;
        }

        if (NeoClient.showGUI){
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                double x = client.player.getX();
                double y = client.player.getY();
                double z = client.player.getZ();
                Vec3d velocity = client.player.getVelocity(); // Get player velocity
                double velocityX = velocity.x;
                double velocityY = velocity.y;
                double velocityZ = velocity.z;
                double tps = getTps(client); // Implement your method to get the TPS

                int xPos = 10;
                int yPos = 10;

                TextRenderer textRenderer = client.textRenderer;
                MatrixStack matrixStack = drawContext.getMatrices();
                VertexConsumerProvider.Immediate vertexConsumers = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                Matrix4f matrix = matrixStack.peek().getPositionMatrix();

                textRenderer.draw("Neo V" + Neo.NEO_VER, xPos, yPos, color.getRGB(), false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
                yPos += 10;
                String formattedX = String.format("%.2f", ((int) (x * 100)) / 100.0);
                String formattedY = String.format("%.2f", ((int) (y * 100)) / 100.0);
                String formattedZ = String.format("%.2f", ((int) (z * 100)) / 100.0);
                int formattedTps = (int) tps;

                textRenderer.draw("X: " + formattedX + " Y: " + formattedY + " Z: " + formattedZ, xPos, yPos, color.getRGB(), false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
                yPos += 10;

                // Display Velocity
                String formattedVelX = String.format("%.2f", velocityX);
                String formattedVelY = String.format("%.2f", velocityY);
                String formattedVelZ = String.format("%.2f", velocityZ);
                textRenderer.draw("Vel: " + formattedVelX + " " + formattedVelY + " " + formattedVelZ, xPos, yPos, color.getRGB(), false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
                yPos += 10;

                int tpsColor;
                if (formattedTps >= 15) {
                    tpsColor = 0x00FF00; // Green
                } else if (formattedTps >= 10) {
                    tpsColor = 0xFFA500; // Orange
                } else {
                    tpsColor = 0xFF0000; // Red
                }

                textRenderer.draw("TPS: " + formattedTps, xPos, yPos, tpsColor, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);

                // Draw module text
                int numOfActive = 0;
                int screenWidth = client.getWindow().getScaledWidth();
                int padding = 10; // Padding from the right edge of the screen
                for (Module m : NeoClient.modules) {
                    int moduleColor = getCategoryColor(m.category);
                    boolean isToggled = m.toggled;
                    int textWidth = textRenderer.getWidth(m.name);
                    int otherSideOfTheScreenWith10Padding = screenWidth - textWidth - padding;
                    if (!m.toggled)
                        continue;
                    textRenderer.draw(m.name, otherSideOfTheScreenWith10Padding, 10 + (numOfActive * 10), moduleColor, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
                    numOfActive += 1;
                }

                vertexConsumers.draw(); // Ensure to call draw() to flush the buffers
            }
        }
    }

    private static int getCategoryColor(Module.Category category) {
        switch (category){
            case COMBAT -> {
                return 0xff0000;
            }
            case MOVEMENT -> {
                return 0x00ff00;
            }
            case PLAYER -> {
                return 0x0000ff;
            }
            case RENDER -> {
                return 0x00FFFF;
            }
            case CLIENT -> {
                return 0xFF00FF;
            }
        }
        return 0x000000;
    }

    private static double getTps(MinecraftClient client) {
        return Neo.tps;
    }
}
