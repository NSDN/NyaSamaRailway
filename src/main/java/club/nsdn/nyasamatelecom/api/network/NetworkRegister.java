package club.nsdn.nyasamatelecom.api.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class NetworkRegister {

    public static void register(Logger logger, SimpleNetworkWrapper wrapper, int discriminatorStart) {
        int discriminator = discriminatorStart;

        NGTPacket.logger = logger;
        wrapper.registerMessage(NGTPacketHandler.class, NGTPacket.class, discriminator, Side.SERVER);
        discriminator += 1;
        wrapper.registerMessage(ParticlePacketHandler.class, ParticlePacket.class, discriminator, Side.CLIENT);
    }

}
