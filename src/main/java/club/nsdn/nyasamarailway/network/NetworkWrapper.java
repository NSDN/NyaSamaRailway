package club.nsdn.nyasamarailway.network;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Created by drzzm32 on 2016.5.16.
 */
public class NetworkWrapper {
    public static SimpleNetworkWrapper instance;

    public NetworkWrapper(FMLPreInitializationEvent event) {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(NyaSamaRailway.MODID);
        instance.registerMessage(TrainPacket.PacketStCHandler.class, TrainPacket.class, 0, Side.CLIENT);
        instance.registerMessage(TrainPacket.PacketCtSHandler.class, TrainPacket.class, 1, Side.SERVER);
        club.nsdn.nyasamatelecom.api.network.NetworkRegister.register(NyaSamaRailway.log, instance, 2);
    }
}
