package club.nsdn.nyasamatelecom.api.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class ParticlePacket implements IMessage {

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

    public void send(SimpleNetworkWrapper wrapper, int dimension) {
        if (wrapper == null) return;
        wrapper.sendToDimension(this, dimension);
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
