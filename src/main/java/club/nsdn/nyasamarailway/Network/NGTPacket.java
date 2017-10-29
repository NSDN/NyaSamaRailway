package club.nsdn.nyasamarailway.Network;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

/**
 * Created by drzzm32 on 2017.10.25.
 */

public class NGTPacket implements IMessage {

    public static class PacketCtSHandler implements IMessageHandler<NGTPacket, IMessage> {
        @Override
        public IMessage onMessage(NGTPacket packet, MessageContext context) {
            EntityPlayerMP serverPlayer = context.getServerHandler().playerEntity;

            NBTTagCompound tagCompound = packet.stack.getTagCompound();
            if (serverPlayer.getCurrentEquippedItem() != null)
                serverPlayer.getCurrentEquippedItem().setTagCompound(tagCompound);

            return null;
        }
    }

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
            NyaSamaRailway.log.error("Couldn't send NGT info", e);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            stack = (new PacketBuffer(buf)).readItemStackFromBuffer();
        } catch (Exception e) {
            NyaSamaRailway.log.error("Couldn't receive NGT info", e);
        }
    }

}
