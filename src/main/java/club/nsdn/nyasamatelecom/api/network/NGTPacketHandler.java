package club.nsdn.nyasamatelecom.api.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class NGTPacketHandler implements IMessageHandler<NGTPacket, IMessage> {
    @Override
    public IMessage onMessage(NGTPacket packet, MessageContext context) {
        EntityPlayerMP serverPlayer = context.getServerHandler().playerEntity;

        NBTTagCompound tagCompound = packet.stack.getTagCompound();
        if (serverPlayer.getCurrentEquippedItem() != null)
            serverPlayer.getCurrentEquippedItem().setTagCompound(tagCompound);

        return null;
    }
}
