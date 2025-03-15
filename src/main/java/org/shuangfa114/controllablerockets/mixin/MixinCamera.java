package org.shuangfa114.controllablerockets.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.controllablerockets.util.RocketTierInformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow
    private float eyeHeightOld;
    @Shadow
    private float eyeHeight;

    @Shadow
    public abstract Entity getEntity();

    @Shadow
    protected abstract void setPosition(double pX, double pY, double pZ);

    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    public void setPos(Camera instance, double pX, double pY, double pZ, Operation<Void> original, @Local(argsOnly = true) float pPartialTick) {
        Entity entity = this.getEntity().getVehicle();
        if (entity instanceof Rocket rocket && rocket.hasLaunched()) {
            Vec3 oldTarget = new Vec3(rocket.xo, rocket.yo, rocket.zo).add(testOld(rocket));
            Vec3 target = rocket.position().add(test(rocket));
            Vec3 vec3 = new Vec3(Mth.lerp(pPartialTick, oldTarget.x, target.x)
                    , Mth.lerp(pPartialTick, oldTarget.y, target.y) + (double) Mth.lerp(pPartialTick, this.eyeHeightOld, this.eyeHeight)
                    , Mth.lerp(pPartialTick, oldTarget.z, target.z));
            this.setPosition(vec3.x, vec3.y, vec3.z);
        } else {
            original.call(instance, pX, pY, pZ);
        }
    }

    @Unique
    public Vec3 test(Rocket entity) {
        return ((EntityInvoker) entity).invokeCalculateViewVector(entity.getXRot() - 90, entity.getYRot()).normalize().scale(RocketTierInformation.getTierByEntity(entity).cameraOffset);
    }

    @Unique
    public Vec3 testOld(Rocket entity) {
        return ((EntityInvoker) entity).invokeCalculateViewVector(entity.xRotO - 90, entity.yRotO).normalize().scale(RocketTierInformation.getTierByEntity(entity).cameraOffset);
    }
}
