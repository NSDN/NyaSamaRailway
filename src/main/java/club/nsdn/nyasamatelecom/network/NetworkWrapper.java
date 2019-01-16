package club.nsdn.nyasamatelecom.network;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class NetworkWrapper {
    public static SimpleNetworkWrapper instance;

    public NetworkWrapper(FMLPreInitializationEvent event) {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(NyaSamaTelecom.MODID);
        club.nsdn.nyasamatelecom.api.network.NetworkRegister.register(NyaSamaTelecom.logger, instance, 0);
    }
}
