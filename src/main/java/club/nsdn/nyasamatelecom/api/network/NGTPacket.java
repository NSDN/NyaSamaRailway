package club.nsdn.nyasamatelecom.api.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.Logger;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class NGTPacket implements IMessage {

    static Logger logger;

    public ItemStack stack;

    public NGTPacket() {
        stack = null;
    }

    public NGTPacket(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            (new PacketBuffer(buf)).writeItemStackToBuffer(stack);
        } catch (Exception e) {
            if (logger != null)
                logger.error("Couldn't send NGT info", e);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            stack = (new PacketBuffer(buf)).readItemStackFromBuffer();
        } catch (Exception e) {
            if (logger != null)
                logger.error("Couldn't receive NGT info", e);
        }
    }

}
