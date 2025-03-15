package org.shuangfa114.controllablerockets.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntity extends CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, IForgeEntity {
    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    @WrapOperation(method = {"turn","absMoveTo(DDDFF)V","teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"},at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    public float noLimit(float pValue, float pMin, float pMax, Operation<Float> original){
        if((Entity)(Object)this instanceof Rocket){
            return pValue;
        }
        return original.call(pValue, pMin, pMax);
    }
}
