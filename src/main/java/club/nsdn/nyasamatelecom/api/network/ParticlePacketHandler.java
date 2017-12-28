package club.nsdn.nyasamatelecom.api.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class ParticlePacketHandler implements IMessageHandler<ParticlePacket, IMessage> {
    @Override
    public IMessage onMessage(ParticlePacket packet, MessageContext context) {
        Minecraft.getMinecraft().theWorld.spawnParticle(
                packet.type,
                packet.x, packet.y, packet.z,
                packet.tX, packet.tY, packet.tZ
        );

        return null;
    }
}
