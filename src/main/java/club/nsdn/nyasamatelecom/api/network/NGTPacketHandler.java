package club.nsdn.nyasamatelecom.api.network;

import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class NGTPacketHandler implements IMessageHandler<NGTPacket, IMessage> {
    @Override
    public IMessage onMessage(NGTPacket packet, MessageContext context) {
        EntityPlayerMP serverPlayer = context.getServerHandler().player;

        NBTTagCompound tagCompound = packet.stack.getTagCompound();
        if (serverPlayer.getHeldItemMainhand().getItem() instanceof NGTablet)
            serverPlayer.getHeldItemMainhand().setTagCompound(tagCompound);

        return null;
    }
}
