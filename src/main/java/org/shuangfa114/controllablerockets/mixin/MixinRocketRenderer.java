package org.shuangfa114.controllablerockets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.math.Axis;
import earth.terrarium.adastra.client.renderers.entities.vehicles.RocketRenderer;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RocketRenderer.class)
public abstract class MixinRocketRenderer extends EntityRenderer<Rocket> {
    protected MixinRocketRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    @ModifyReceiver(method = "render(Learth/terrarium/adastra/common/entities/vehicles/Rocket;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;",ordinal = 01))
    public Axis test(Axis instance, float pDegrees){
        return Axis.XP;
    }
}
