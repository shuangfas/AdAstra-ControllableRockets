package org.shuangfa114.controllablerockets.mixin;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.shuangfa114.controllablerockets.util.ILockable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class,priority = 900)
public abstract class MixinPlayer {
    @Inject(method = "wantsToStopRiding",at = @At("HEAD"), cancellable = true)
    public void cannotStopRidingWhenWelding(CallbackInfoReturnable<Boolean> cir){
        Player player = (Player)(Object)this;
        Entity vehicle = player.getVehicle();
        if(player.isShiftKeyDown()&&vehicle instanceof Rocket rocket){
            if(((ILockable)rocket).isLock()){
                player.displayClientMessage(Component.translatable("message.controllable_rockets.cannot_stop_riding").withStyle(ChatFormatting.RED),true);
                cir.setReturnValue(false);
            }
        }
    }
}
