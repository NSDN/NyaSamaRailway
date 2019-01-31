package club.nsdn.nyasamaoptics.network;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class NetworkWrapper {
    public static SimpleNetworkWrapper instance;

    public NetworkWrapper(FMLPreInitializationEvent event) {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(NyaSamaOptics.MODID);
        club.nsdn.nyasamatelecom.api.network.NetworkRegister.register(NyaSamaOptics.logger, instance, 0);
    }
}
