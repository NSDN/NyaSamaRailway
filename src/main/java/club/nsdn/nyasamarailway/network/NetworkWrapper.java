package club.nsdn.nyasamarailway.network;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NetworkWrapper {
    public static SimpleNetworkWrapper instance;

    public NetworkWrapper(FMLPreInitializationEvent event) {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(NyaSamaRailway.MODID);
        instance.registerMessage(PacketStCHandler.class, TrainPacket.class, 0, Side.CLIENT);
        instance.registerMessage(PacketCtSHandler.class, TrainPacket.class, 1, Side.SERVER);
        club.nsdn.nyasamatelecom.api.network.NetworkRegister.register(NyaSamaRailway.logger, instance, 2);
    }
}
