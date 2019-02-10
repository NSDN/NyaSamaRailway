package club.nsdn.nyasamarailway.event;

import club.nsdn.nyasamarailway.api.cart.AbsLocoBase;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.network.PacketStCHandler;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NTPCtrlHandler {
    private static NTPCtrlHandler instance;

    public static NTPCtrlHandler instance() {
        if (instance == null)
            instance = new NTPCtrlHandler();
        return instance;
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        if (player == null) return;
        if (PacketStCHandler.player == null) PacketStCHandler.player = player;

        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat)
            return;

        ItemStack stack = player.getHeldItemMainhand();
        if (!stack.isEmpty()) {
            TrainPacket packet = new TrainPacket();
            if (stack.getItem() instanceof ItemNTP8Bit) {
                ItemNTP8Bit ntp8Bit = (ItemNTP8Bit) stack.getItem();
                packet.fromStack(stack);

                EntityMinecart cart = packet.getCartInClient(ntp8Bit.cart.get(stack));
                if (cart != null) {
                    if (cart instanceof AbsLocoBase) {
                        TrainController.doControl(packet, player);
                        NetworkWrapper.instance.sendToServer(packet);
                        packet.toStack(stack);
                        return;
                    }
                    TrainController.doControl(packet, player);
                    NetworkWrapper.instance.sendToServer(packet);
                    packet.toStack(stack);
                }
            } else if (stack.getItem() instanceof ItemNTP32Bit) {
                ItemNTP32Bit ntp32Bit = (ItemNTP32Bit) stack.getItem();
                packet.fromStack(stack);

                int[] carts = ntp32Bit.carts.get(stack);
                if (carts.length == 1 && carts[0] == -1)
                    return;
                TrainController.doControl(packet, player);
                NetworkWrapper.instance.sendToServer(packet);
                packet.toStack(stack);
            }
        }
    }
}
