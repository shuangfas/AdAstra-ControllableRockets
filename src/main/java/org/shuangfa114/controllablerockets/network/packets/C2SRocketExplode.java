package org.shuangfa114.controllablerockets.network.packets;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRocketExplode {
    private final int id;
    public C2SRocketExplode(int id){
        this.id=id;
    }
    public C2SRocketExplode(FriendlyByteBuf buffer) {
        this.id=buffer.readInt();
    }
    public static void encode(C2SRocketExplode message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.id);
    }
    public static void handle(C2SRocketExplode message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(()->{
            NetworkEvent.Context context = supplier.get();
            Entity entity = context.getSender().level().getEntity(message.id);
            if(entity instanceof Rocket rocket){
                rocket.explode();
                context.setPacketHandled(true);
            }
        });
    }
}
