package club.nsdn.nyasamatelecom.api.network;

import com.google.common.base.Charsets;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

/**
 * Created by drzzm32 on 2018.12.13.
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
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeInt(type.length());
        buffer.writeString(type);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeDouble(tX);
        buffer.writeDouble(tY);
        buffer.writeDouble(tZ);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        int length = buffer.readInt();
        type = buffer.readString(length);
        x = buffer.readDouble();
        y = buffer.readDouble();
        z = buffer.readDouble();
        tX = buffer.readDouble();
        tY = buffer.readDouble();
        tZ = buffer.readDouble();
    }

}
