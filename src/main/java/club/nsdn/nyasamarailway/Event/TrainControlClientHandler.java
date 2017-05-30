package club.nsdn.nyasamarailway.Event;

import club.nsdn.nyasamarailway.Entity.LocoBase;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import club.nsdn.nyasamarailway.TrainControl.NetworkWrapper;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

/**
 * Created by drzzm32 on 2016.5.13.
 */

public class TrainControlClientHandler {
    private static TrainControlClientHandler instance;

    public static TrainControlClientHandler instance() {
        if (instance == null)
            instance = new TrainControlClientHandler();
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
            if (stack.getItem() instanceof ItemTrainController8Bit) {
                if (ToolHandler.controller8Bit != null) {
                    EntityMinecart cart = ToolHandler.controller8Bit.getCartInClient();
                    if (cart != null) {
                        if (cart instanceof LocoBase) {
                            TrainController.doControl(ToolHandler.controller8Bit, player);
                            ((LocoBase) cart).setTrainPacket(ToolHandler.controller8Bit);
                            NetworkWrapper.packetSender.sendToServer(ToolHandler.controller8Bit);
                            return;
                        }
                        TrainController.doControl(ToolHandler.controller8Bit, player);
                        TrainController.doMotion(ToolHandler.controller8Bit, cart);
                        NetworkWrapper.packetSender.sendToServer(ToolHandler.controller8Bit);
                    }
                }
            } else if (stack.getItem() instanceof ItemTrainController32Bit) {
                if (ToolHandler.controller32Bit != null) {
                    EntityMinecart cart;
                    if (!ToolHandler.controller32Bit.trainUnits.isEmpty()) {
                        TrainController.doControl(ToolHandler.controller32Bit, player);
                        for (int i : ToolHandler.controller32Bit.trainUnits) {
                            cart = ToolHandler.controller32Bit.getCartInClient(i);
                            if (cart != null) {
                                TrainController.doMotion(ToolHandler.controller32Bit, cart);
                            }
                        }
                        NetworkWrapper.packetSender.sendToServer(ToolHandler.controller32Bit);
                    }
                }
            }
        }
    }
}