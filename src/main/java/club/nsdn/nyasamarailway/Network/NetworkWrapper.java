package club.nsdn.nyasamarailway.Network;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Created by drzzm32 on 2016.5.16.
 */
public class NetworkWrapper {
    public static SimpleNetworkWrapper packetSender;

    public NetworkWrapper(FMLPreInitializationEvent event) {
        packetSender = NetworkRegistry.INSTANCE.newSimpleChannel(NyaSamaRailway.MODID);
        packetSender.registerMessage(TrainPacket.PacketStCHandler.class, TrainPacket.class, 0, Side.CLIENT);
        packetSender.registerMessage(TrainPacket.PacketCtSHandler.class, TrainPacket.class, 1, Side.SERVER);
        packetSender.registerMessage(NGTPacket.PacketCtSHandler.class, NGTPacket.class, 2, Side.SERVER);
        packetSender.registerMessage(ParticlePacket.PacketStCHandler.class, ParticlePacket.class, 3, Side.CLIENT);
    }
}
