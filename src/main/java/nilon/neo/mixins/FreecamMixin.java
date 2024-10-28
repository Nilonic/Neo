package nilon.neo.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import nilon.neo.client.NeoClient;
import nilon.neo.entities.FreecamEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.GameRenderer.class)
public abstract class FreecamMixin implements java.lang.AutoCloseable {

    @Shadow @Final private LightmapTextureManager lightmapTextureManager;
    @Shadow @Final private Camera camera;

    @Shadow protected abstract boolean shouldRenderBlockOutline();

    @Shadow @Final
    MinecraftClient client;
    @Shadow private float viewDistance;

    @Shadow protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow public abstract Matrix4f getBasicProjectionMatrix(double fov);

    @Shadow protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Shadow protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow private int ticks;

    @Shadow public abstract void loadProjectionMatrix(Matrix4f projectionMatrix);

    @Unique
    private FreecamEntity freecamEntity = null;
    @Unique
    private Vec3d freecamMovement = Vec3d.ZERO;

    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
    public void onRenderWorld(float tickDelta, long limitTime, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        PlayerEntity player = client.player;
        ClientWorld world = client.world;

        if (NeoClient.isFreecam) {
            if (freecamEntity == null) {
                // Initialize freecam entity at player's position and copy orientation
                Vec3d playerPosition = player.getPos();
                freecamEntity = new FreecamEntity(world);
                freecamEntity.updatePosition(playerPosition);
                freecamEntity.setYaw(player.getYaw());
                freecamEntity.setPitch(player.getPitch());
                this.lightmapTextureManager.update(tickDelta);
                client.setCameraEntity(freecamEntity);  // Set camera to freecam
                client.setCameraEntity(freecamEntity);

                boolean bl = this.shouldRenderBlockOutline();

                Camera camera = this.camera;
                Entity entity = this.client.getCameraEntity() == null ? freecamEntity : this.client.getCameraEntity();
                assert this.client.world != null;
                camera.update(this.client.world, entity, !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), this.client.world.getTickManager().shouldSkipTick(entity) ? 1.0F : tickDelta);
                this.viewDistance = (float)(this.client.options.getClampedViewDistance() * 16);
                double d = this.getFov(camera, tickDelta, true);
                Matrix4f matrix4f = this.getBasicProjectionMatrix(d);
                MatrixStack matrixStack = new MatrixStack();
                this.tiltViewWhenHurt(matrixStack, camera.getLastTickDelta());
                if (this.client.options.getBobView().getValue()) {
                    this.bobView(matrixStack, camera.getLastTickDelta());
                }
                matrix4f.mul(matrixStack.peek().getPositionMatrix());
                float f = this.client.options.getDistortionEffectScale().getValue().floatValue();
                assert this.client.player != null;
                float g = MathHelper.lerp(tickDelta, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity) * f * f;
                if (g > 0.0F) {
                    int i = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
                    float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
                    h *= h;
                    Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
                    float j = ((float)this.ticks + tickDelta) * (float)i * 0.017453292F;
                    matrix4f.rotate(j, vector3f);
                    matrix4f.scale(1.0F / h, 1.0F, 1.0F);
                    matrix4f.rotate(-j, vector3f);
                }

                this.loadProjectionMatrix(matrix4f);
                Matrix4f matrix4f2 = (new Matrix4f()).rotationXYZ(camera.getPitch() * 0.017453292F, camera.getYaw() * 0.017453292F + 3.1415927F, 0.0F);
                this.client.worldRenderer.setupFrustum(camera.getPos(), matrix4f2, this.getBasicProjectionMatrix(Math.max(d, (double) this.client.options.getFov().getValue())));
                this.client.worldRenderer.render(tickDelta, limitTime, bl, camera, this.client.gameRenderer, this.lightmapTextureManager, matrix4f2, matrix4f);
                this.client.getProfiler().swap("hand");

                this.client.getProfiler().pop();
            }

            // Smoothly interpolate freecam movement
            if (freecamEntity != null) {
                Vec3d newPos = freecamEntity.getPos().add(freecamMovement);
                freecamEntity.updatePosition(newPos);
            }
            ci.cancel();

        } else {
            // Exit freecam and return control to player
            client.setCameraEntity(player);  // Restore player as the camera
            player.noClip = false;
            freecamEntity = null;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (NeoClient.isFreecam && freecamEntity != null) {
            // Handle freecam movement input, calculate new movement vector
            freecamMovement = Vec3d.ZERO;
            if (client.options.forwardKey.isPressed()) {
                freecamMovement = freecamMovement.add(freecamEntity.getRotationVector().multiply(0.1));
            }
            if (client.options.backKey.isPressed()) {
                freecamMovement = freecamMovement.subtract(freecamEntity.getRotationVector().multiply(0.1));
            }
            if (client.options.leftKey.isPressed()) {
                freecamMovement = freecamMovement.add(freecamEntity.getRotationVector().rotateY((float) Math.toRadians(90)).multiply(0.1));
            }
            if (client.options.rightKey.isPressed()) {
                freecamMovement = freecamMovement.add(freecamEntity.getRotationVector().rotateY((float) Math.toRadians(-90)).multiply(0.1));
            }
        }
    }
}
