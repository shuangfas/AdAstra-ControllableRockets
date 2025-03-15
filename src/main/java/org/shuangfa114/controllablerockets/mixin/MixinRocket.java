package org.shuangfa114.controllablerockets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.botarium.common.menu.ExtraDataMenuProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.shuangfa114.controllablerockets.Config;
import org.shuangfa114.controllablerockets.ControllableRockets;
import org.shuangfa114.controllablerockets.WeldingToolsItem;
import org.shuangfa114.controllablerockets.network.packets.C2SRocketExplode;
import org.shuangfa114.controllablerockets.network.packets.C2SRocketXRot;
import org.shuangfa114.controllablerockets.util.ILockable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

import static earth.terrarium.adastra.common.entities.vehicles.Rocket.LAUNCH_TICKS;

@Mixin(value = Rocket.class, remap = false)
public abstract class MixinRocket extends Entity implements PlayerRideable, ExtraDataMenuProvider, HasCustomInventoryScreen, ILockable {

    @Unique
    private static final EntityDataAccessor<Float> SPEED;
    @Unique
    private static final EntityDataAccessor<Integer> ACCELERATION_TICK;

    static {
        SPEED = SynchedEntityData.defineId(MixinRocket.class, EntityDataSerializers.FLOAT);
        ACCELERATION_TICK = SynchedEntityData.defineId(MixinRocket.class, EntityDataSerializers.INT);
    }

    @Unique
    private float xRotj;
    @Unique
    private float weldingTick;
    @Unique
    private boolean isLock;

    public MixinRocket(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract int tier();

    @Shadow
    public abstract boolean hasLaunched();

    @Inject(method = "initiateLaunchSequence", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void debugTicksSetting(CallbackInfo ci) {
        this.entityData.set(LAUNCH_TICKS, Config.launchTick);
    }

    @Inject(method = "flightTick", at = @At(value = "INVOKE", target = "Learth/terrarium/adastra/common/entities/vehicles/Rocket;xxa()F"))
    public void addZza(CallbackInfo ci) {
        if(this.level().isClientSide){
            float zza = this.zza();
            if (zza != 0) {
                float cache = (float) (zza * this.getSpeed() / Config.maxRocketSpeed * 1.1);
                xRotj += Config.reverseXRotControl?cache*-1:cache;
            } else {
                xRotj *= 0.8F;
            }
            this.xRotj = Mth.clamp(this.xRotj, -3.0F, 3.0F);
            this.setXRot(this.getXRot() + xRotj);
            ControllableRockets.network.sendToServer(new C2SRocketXRot(this.getId(),this.getXRot()));
        }
    }

    @WrapOperation(method = "flightTick", at = @At(value = "INVOKE", target = "Learth/terrarium/adastra/common/entities/vehicles/Rocket;setDeltaMovement(DDD)V"))
    public void setDeltaWithLookAngle(Rocket instance, double x, double y, double z, Operation<Void> original) {
        Vec3 lookAngle = ((EntityInvoker) this).invokeCalculateViewVector(this.getXRot() - 90, this.getYRot());
        instance.setDeltaMovement(lookAngle.scale(getSpeed()));
        if (this.getAccelerationTick() < Config.maxRocketAccelerationTick) {
            this.entityData.set(ACCELERATION_TICK, this.getAccelerationTick() + 1);
        }
    }

    @WrapOperation(method = "spawnRocketParticles", at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 0), @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 1)})
    public void particleSpawnPos(Level instance, ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Operation<Void> original) {
        Vec3 newPosition = getRotationPosition(90, 2.5F, getDoorPosition());
        Vec3 speed = calculateViemVector(90.0f).normalize().multiply(new Vec3(pXSpeed, pYSpeed, pZSpeed));
        instance.addParticle(pParticleData, newPosition.x, newPosition.y, newPosition.z, speed.x, speed.y, speed.z);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        Vec3 vec3Min = getRotationPosition(90.0f, 2, this.position().add(-this.getBbWidth() / 2, 2, -this.getBbWidth() / 2));
        Vec3 vec3Max = getRotationPosition(-90.0f, this.getBbHeight() - 2, this.position().add(this.getBbWidth() / 2, 2, this.getBbWidth() / 2));
        return new AABB(vec3Min, vec3Max);
    }

    @ModifyReturnValue(method = "isObstructed", at = @At("RETURN"))
    public boolean crash(boolean original) {
        Vec3 normalize = calculateViemVector(-90.0f).normalize();
        Vec3 delta = normalize.scale(this.getBbHeight() - 1.5F);
        BlockHitResult hitResult = this.level().clip(new ClipContext(getDoorPosition().add(delta), getDoorPosition().add(delta.scale(1.2)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        if (this.level().isClientSide) {
            if (!this.level().getBlockState(hitResult.getBlockPos()).isAir()) {
                ControllableRockets.network.sendToServer(new C2SRocketExplode(this.getId()));
            }
        }
        return false;
    }

    @Inject(method = "flightTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    public void speedModify(CallbackInfo ci) {
        this.entityData.set(SPEED, (float) (1 - Math.cos(((double) this.getAccelerationTick() / Config.maxRocketAccelerationTick * Math.PI) / 2)) * Config.maxRocketSpeed);
    }


    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void welding(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack itemStack1 = player.getItemInHand(InteractionHand.OFF_HAND);
        if ((itemStack.is(ControllableRockets.WELDING_TOOLS.get()) || itemStack1.is(ControllableRockets.WELDING_TOOLS.get())) && !this.hasLaunched()) {
            if (!isLock) {
                if (weldingTick >= this.tier() * 5) {
                    isLock = true;
                    weldingTick = 0;
                    player.displayClientMessage(Component.translatable("message.controllable_rockets.welding_success").withStyle(ChatFormatting.RED), true);
                } else {
                    if (this.level().isClientSide) {
                        double length = this.position().add(0, 1.9, 0).subtract(player.position()).length();
                        Vec3 particlePos = player.position().add(player.getLookAngle().normalize().scale(length - 0.5));
                        Vec3 speed = getSamexyzVec3(Mth.nextDouble(this.level().random, -0.05, 0.05));
                        for (int i = 0; i < 15; i++) {
                            this.level().addParticle(ParticleTypes.LAVA, particlePos.x, particlePos.y, particlePos.z, speed.x, speed.y, speed.z);
                        }
                    }
                    weldingTick++;
                    float seconds = (this.tier() * 5 - weldingTick) / 5.0F;
                    if (!itemStack.isEmpty()) {
                        ((WeldingToolsItem) itemStack.getItem()).getEnergyStorage(itemStack).internalExtract(10, false);
                    } else {
                        ((WeldingToolsItem) itemStack1.getItem()).getEnergyStorage(itemStack).internalExtract(10, false);
                    }
                    player.displayClientMessage(Component.translatable("message.controllable_rockets.welding_times", seconds).withStyle(ChatFormatting.RED), true);
                }
            }
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @WrapOperation(method = "burnEntitiesUnderRocket", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    public <T extends Entity> List<T> modifyBurnedAABB(Level instance, Class<T> aClass, AABB aabb, Predicate<T> predicate, Operation<List<T>> original) {
        Vec3 vec3Min = getRotationPosition(90.0f, 2, this.position().add(-this.getBbWidth() / 2, 2, -this.getBbWidth() / 2));
        Vec3 vec3Max = getRotationPosition(90.0f, 30, this.position().add(this.getBbWidth() / 2, 2, this.getBbWidth() / 2));
        List<T> list = original.call(instance, aClass, new AABB(vec3Min, vec3Max), predicate);
        list.removeIf((entity) -> entity.getBoundingBox().clip(vec3Min, vec3Max).isEmpty());
        return list;
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void defineCustomSynchedData(CallbackInfo ci) {
        this.entityData.define(SPEED, 0.05F);
        this.entityData.define(ACCELERATION_TICK, 0);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void readCustomData(CompoundTag compound, CallbackInfo ci) {
        this.entityData.set(SPEED, compound.getFloat("Speed"));
        this.entityData.set(ACCELERATION_TICK, compound.getInt("AccelerationTick"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void addCustomData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("AccelerationTick", getAccelerationTick());
    }


    @Unique
    public float zza() {
        LivingEntity controllingPassenger = this.getControllingPassenger();
        return controllingPassenger == null ? 0.0F : controllingPassenger.zza;
    }

    @Unique
    public Vec3 calculateViemVector(float t) {
        return ((EntityInvoker) this).invokeCalculateViewVector(this.getXRot() + t, this.getYRot());
    }


    @Unique
    public Vec3 getRotationPosition(float t, float distance, Vec3 position) {
        Vec3 normalize = calculateViemVector(t).normalize();
        Vec3 delta = normalize.scale(distance);
        return position.add(delta);
    }

    @Unique
    public Vec3 getSamexyzVec3(double t) {
        return new Vec3(t, t, t);
    }

    @Unique
    public Vec3 getDoorPosition() {
        return this.position().add(0, 1.75, 0);
    }

    @Unique
    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    @Unique
    public int getAccelerationTick() {
        return this.entityData.get(ACCELERATION_TICK);
    }

    @Override
    public boolean isLock() {
        return isLock;
    }
}
