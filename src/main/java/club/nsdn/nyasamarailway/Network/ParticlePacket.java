package club.nsdn.nyasamarailway.Network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

/**
 * Created by drzzm32 on 2017.10.29.
 */
public class ParticlePacket implements IMessage {

    public static class PacketStCHandler implements IMessageHandler<ParticlePacket, IMessage> {
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

    public String type;
    public double x, y, z;
    public double tX, tY, tZ;

    public ParticlePacket() {
        type = "null";
        x = y = z = 0;
        tX = tY = tZ = 0;
    }

    public ParticlePacket(String type, double x, double y, double z, double tX, double tY, double tZ) {
        this.type = type;
        this.x = x; this.y = y; this.z = z;
        this.tX = tX; this.tY = tY; this.tZ = tZ;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.length());
        buf.writeBytes(type.getBytes());
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(tX);
        buf.writeDouble(tY);
        buf.writeDouble(tZ);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        type = new String(buf.readBytes(length).array(), Charsets.UTF_8);
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        tX = buf.readDouble();
        tY = buf.readDouble();
        tZ = buf.readDouble();
    }

}
