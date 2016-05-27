/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package club.nsdn.nyasamarailway.Event;

import club.nsdn.nyasamarailway.Entity.TrainBase;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import club.nsdn.nyasamarailway.TrainControl.NetworkWrapper;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class ToolHandler {
    private static ToolHandler instance;
    public static TrainPacket controller8Bit;
    public static TrainPacket controller32Bit;

    public static ToolHandler instance() {
        if (instance == null)
            instance = new ToolHandler();
        return instance;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
            Entity entity = event.target;

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemTrainController8Bit) {
                    if (player.isSneaking()) {
                        controller8Bit = new TrainPacket();
                        NetworkWrapper.packetSender.sendTo(ToolHandler.controller8Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.cleared"));
                        return;
                    }
                    if (!(entity instanceof TrainBase) && entity instanceof EntityMinecart) {
                        controller8Bit = new TrainPacket(player.getEntityId(), entity.getEntityId(), 0, 5, 0);
                        NetworkWrapper.packetSender.sendTo(controller8Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
                    }
                } else if (stack.getItem() instanceof ItemTrainController32Bit) {
                    if (controller32Bit == null) {
                        controller32Bit = new TrainPacket(-1, 0, 5, 0);
                    }
                    controller32Bit.isUnits = true;

                    if (player.isSneaking()) {
                        controller32Bit.trainUnits.clear();
                        controller32Bit.cartID = -1;
                        NetworkWrapper.packetSender.sendTo(controller32Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.cleared"));
                        return;
                    }
                    if (!(entity instanceof TrainBase) && entity instanceof EntityMinecart) {
                        controller32Bit.cartID = entity.getEntityId();
                        if (!controller32Bit.trainUnits.contains(controller32Bit.cartID)) {
                            controller32Bit.trainUnits.add(controller32Bit.cartID);
                        }
                        controller32Bit.playerID = player.getEntityId();
                        NetworkWrapper.packetSender.sendTo(controller32Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
                    }
                }
            }
        }
    }
}
