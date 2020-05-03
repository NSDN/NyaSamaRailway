package club.nsdn.nyasamarailway.network;

import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class PacketStCHandler implements IMessageHandler<TrainPacket, IMessage> {

    public static EntityPlayer player;

    public boolean processPacket(ItemStack stack, TrainPacket packet) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemNTP8Bit) {
                ItemNTP8Bit ntp8Bit = (ItemNTP8Bit) stack.getItem();
                ntp8Bit.power.set(stack, packet.P);
                ntp8Bit.brake.set(stack, packet.R);
                ntp8Bit.dir.set(stack, packet.Dir);
                ntp8Bit.mode.set(stack, packet.Mode);
                ntp8Bit.mblk.set(stack, packet.MBlk);
                return true;
            } else if (stack.getItem() instanceof ItemNTP32Bit) {
                ItemNTP32Bit ntp32Bit = (ItemNTP32Bit) stack.getItem();
                ntp32Bit.power.set(stack, packet.P);
                ntp32Bit.brake.set(stack, packet.R);
                ntp32Bit.dir.set(stack, packet.Dir);
                return true;
            }
        }
        return false;
    }

    @Override
    public IMessage onMessage(TrainPacket packet, MessageContext context) {
        if (player == null) return null;

        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (!processPacket(player.getHeldItemMainhand(), packet))
                processPacket(player.getHeldItemOffhand(), packet);
        });

        return null;
    }
}
