package club.nsdn.nyasamarailway.event;

import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by drzzm32 on 2016.5.13.
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
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player == null)
        return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat)
            return;

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null) {
            TrainPacket packet = new TrainPacket();
            if (stack.getItem() instanceof ItemNTP8Bit) {
                ItemNTP8Bit ntp8Bit = (ItemNTP8Bit) stack.getItem();
                packet.fromStack(stack);

                EntityMinecart cart = packet.getCartInClient(ntp8Bit.cart.get(stack));
                if (cart != null) {
                    if (cart instanceof LocoBase) {
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
