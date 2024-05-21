package nilon.neo.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import nilon.neo.Neo;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void getAlternativeWindowTitle(@NotNull CallbackInfoReturnable<String> ci){
        String builder = "Neo V "+ Neo.NEO_VER + " | Minecraft {version}".replace("{version}", SharedConstants.getGameVersion().getId());


        ci.setReturnValue(builder);
    }
}