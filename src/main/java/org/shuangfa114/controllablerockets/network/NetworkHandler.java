package org.shuangfa114.controllablerockets.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.shuangfa114.controllablerockets.ControllableRockets;
import org.shuangfa114.controllablerockets.network.packets.C2SRocketExplode;
import org.shuangfa114.controllablerockets.network.packets.C2SRocketXRot;

public class NetworkHandler {
    @SuppressWarnings("removal")
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(ControllableRockets.MODID, "network");
    public static final String version="114";
    public static SimpleChannel simpleChannel = NetworkRegistry.newSimpleChannel(CHANNEL_NAME,()->version,version::equals,version::equals);
    public static SimpleChannel getNetWork(){
        int id = 0;
        simpleChannel.registerMessage(id++, C2SRocketExplode.class,C2SRocketExplode::encode,C2SRocketExplode::new,C2SRocketExplode::handle);
        simpleChannel.registerMessage(id++, C2SRocketXRot.class,C2SRocketXRot::encode,C2SRocketXRot::new,C2SRocketXRot::handle);
        return simpleChannel;
    }
}
