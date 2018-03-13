package club.nsdn.nyasamarailway.network;

import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Created by drzzm32 on 2018.3.13.
 */
public class PacketCtSHandler implements IMessageHandler<TrainPacket, IMessage> {
    @Override
    public IMessage onMessage(TrainPacket packet, MessageContext context) {
        EntityPlayerMP serverPlayer = context.getServerHandler().playerEntity;

        ItemStack stack = serverPlayer.getCurrentEquippedItem();
        if (stack != null) {
            if (stack.getItem() instanceof ItemNTP8Bit) {
                ItemNTP8Bit ntp8Bit = (ItemNTP8Bit) stack.getItem();
                ntp8Bit.power.set(stack, packet.P);
                ntp8Bit.brake.set(stack, packet.R);
                ntp8Bit.dir.set(stack, packet.Dir);
            } else if (stack.getItem() instanceof ItemNTP32Bit) {
                ItemNTP32Bit ntp32Bit = (ItemNTP32Bit) stack.getItem();
                ntp32Bit.power.set(stack, packet.P);
                ntp32Bit.brake.set(stack, packet.R);
                ntp32Bit.dir.set(stack, packet.Dir);
            }
        }

        return null;
    }
}
