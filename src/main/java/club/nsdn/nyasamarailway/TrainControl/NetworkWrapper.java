package club.nsdn.nyasamarailway.TrainControl;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class NetworkWrapper {
    public static SimpleNetworkWrapper packetSender;

    public NetworkWrapper(FMLPreInitializationEvent event) {
        packetSender = NetworkRegistry.INSTANCE.newSimpleChannel(NyaSamaRailway.MODID);
        packetSender.registerMessage(TrainPacket.PacketStCHandler.class, TrainPacket.class, 0, Side.CLIENT);
        packetSender.registerMessage(TrainPacket.PacketCtSHandler.class, TrainPacket.class, 1, Side.SERVER);
    }
}
