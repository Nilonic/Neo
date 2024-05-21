package nilon.neo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class HUD {

    public static void renderHud(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();
            double tps = getTps(client); // Implement your method to get the TPS

            int xPos = 10;
            int yPos = 10;

            TextRenderer textRenderer = client.textRenderer;
            MatrixStack matrixStack = drawContext.getMatrices();
            VertexConsumerProvider.Immediate vertexConsumers = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            Matrix4f matrix = matrixStack.peek().getPositionMatrix();

            textRenderer.draw("Neo V" + Neo.NEO_VER, xPos, yPos, 0xFFFFFF, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            yPos += 10;
            String formattedX = String.format("%.2f", ((int)(x * 100)) / 100.0);
            String formattedY = String.format("%.2f", ((int)(y * 100)) / 100.0);
            String formattedZ = String.format("%.2f", ((int)(z * 100)) / 100.0);
            int formattedTps = (int) tps;

            textRenderer.draw("X: " + formattedX + " Y: " + formattedY + " Z: " + formattedZ, xPos, yPos, 0xFFFFFF, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
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


            // Example module name
            String moduleName = "ModuleName";

            // Get the screen width
            int screenWidth = client.getWindow().getScaledWidth();

            // Calculate the x position (right-anchored with 10 pixels padding)
            int padding = 10;
            int textWidth = textRenderer.getWidth(moduleName);
            xPos = screenWidth - textWidth - padding;

            // Fixed y position with 10 pixels padding
            yPos = 10;

            // Draw the text
            textRenderer.draw(moduleName, xPos, yPos, 0xFFFFFF, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);

            vertexConsumers.draw(); // Ensure to call draw() to flush the buffers
        }
    }

    private static double getTps(MinecraftClient client) {
        return Neo.tps;
    }
}
