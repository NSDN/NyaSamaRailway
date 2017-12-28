package club.nsdn.nyasamatelecom.api.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

public class NetworkRegister {

    public static void register(Logger logger, SimpleNetworkWrapper wrapper, int discriminatorStart) {
        int discriminator = discriminatorStart;

        NGTPacket.logger = logger;
        wrapper.registerMessage(NGTPacketHandler.class, NGTPacket.class, discriminator, Side.SERVER);
        discriminator += 1;
        wrapper.registerMessage(ParticlePacketHandler.class, ParticlePacket.class, discriminator, Side.CLIENT);
    }

}
