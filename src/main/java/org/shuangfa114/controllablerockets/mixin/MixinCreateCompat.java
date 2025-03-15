package org.shuangfa114.controllablerockets.mixin;

import earth.terrarium.adastra.common.compat.create.CreateCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateCompat.class)
public abstract class MixinCreateCompat {
    @Inject(method = "init",at = @At("HEAD"), cancellable = true,remap = false)
    private static void cancel(CallbackInfo ci){
        ci.cancel();
    }
}
