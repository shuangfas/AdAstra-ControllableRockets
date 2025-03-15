package org.shuangfa114.controllablerockets.network.packets;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRocketXRot {
    private final int id;
    private final float xRot;
    public C2SRocketXRot(int id,float xRot){
        this.id=id;
        this.xRot=xRot;
    }
    public C2SRocketXRot(FriendlyByteBuf buffer) {
        this.id=buffer.readInt();
        this.xRot=buffer.readFloat();
    }
    public static void encode(C2SRocketXRot message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.id);
        buffer.writeFloat(message.xRot);
    }
    public static void handle(C2SRocketXRot message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(()->{
            NetworkEvent.Context context = supplier.get();
            Entity entity = context.getSender().level().getEntity(message.id);
            if(entity instanceof Rocket rocket){
                rocket.setXRot(message.xRot);
                context.setPacketHandled(true);
            }
        });
    }
}
