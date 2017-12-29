package club.nsdn.nyasamarailway.event;

import club.nsdn.nyasamarailway.entity.*;
import club.nsdn.nyasamarailway.entity.cart.NSPCT5;
import club.nsdn.nyasamarailway.entity.cart.NSPCT5L;
import club.nsdn.nyasamarailway.entity.loco.NSPCT8J;
import club.nsdn.nyasamarailway.extmod.ExRollerCoaster;
import club.nsdn.nyasamarailway.extmod.RailsOfWar;
import club.nsdn.nyasamarailway.item.cart.ItemNSPCT5;
import club.nsdn.nyasamarailway.item.cart.ItemNSPCT5L;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemTrainController8Bit;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.network.TrainPacket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

/**
 * Created by drzzm32 on 2016.6.7.
 */
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
                        NetworkWrapper.instance.sendTo(ToolHandler.controller8Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.cleared"));
                        return;
                    }
                    if (entity instanceof EntityMinecart) {
                        controller8Bit = new TrainPacket(player.getEntityId(), entity.getEntityId(), 0, 5, 0);
                        NetworkWrapper.instance.sendTo(controller8Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
                    }
                }

                else if (stack.getItem() instanceof ItemTrainController32Bit) {
                    if (controller32Bit == null) {
                        controller32Bit = new TrainPacket(-1, 0, 5, 0);
                    }
                    controller32Bit.isUnits = true;

                    if (player.isSneaking()) {
                        controller32Bit.trainUnits.clear();
                        controller32Bit.cartID = -1;
                        NetworkWrapper.instance.sendTo(controller32Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.cleared"));
                        return;
                    }
                    if (entity instanceof EntityMinecart) {
                        if (entity instanceof LocoBase) {
                            player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.error"));
                            return;
                        }
                        controller32Bit.cartID = entity.getEntityId();
                        if (!controller32Bit.trainUnits.contains(controller32Bit.cartID)) {
                            controller32Bit.trainUnits.add(controller32Bit.cartID);
                        }
                        controller32Bit.playerID = player.getEntityId();
                        NetworkWrapper.instance.sendTo(controller32Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
                    } else {
                        if (ExRollerCoaster.getInstance() != null) {
                            if (ExRollerCoaster.getInstance().verifyEntity(entity)) {
                                controller32Bit.cartID = entity.getEntityId();
                                if (!controller32Bit.trainUnits.contains(controller32Bit.cartID)) {
                                    controller32Bit.trainUnits.add(controller32Bit.cartID);
                                }
                                controller32Bit.playerID = player.getEntityId();
                                NetworkWrapper.instance.sendTo(controller32Bit, player);
                                player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
                            }
                        }
                        if (RailsOfWar.getInstance() != null) {
                            if (RailsOfWar.getInstance().verifyEntity(entity)) {
                                controller32Bit.cartID = entity.getEntityId();
                                if (!controller32Bit.trainUnits.contains(controller32Bit.cartID)) {
                                    controller32Bit.trainUnits.add(controller32Bit.cartID);
                                }
                                controller32Bit.playerID = player.getEntityId();
                                NetworkWrapper.instance.sendTo(controller32Bit, player);
                                player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
                            }
                        }
                    }
                }

                else if (stack.getItem() instanceof Item1N4148) {
                    if (entity instanceof NSPCT8J) {
                        NSPCT8J loco = (NSPCT8J) entity;
                        loco.setHighSpeedMode(!loco.getHighSpeedMode());
                        player.addChatComponentMessage(new ChatComponentTranslation(
                                "info.nspc8j.mode", String.valueOf(loco.getHighSpeedMode()).toUpperCase()));
                    }
                }

                else if (stack.getItem() instanceof ItemNSPCT5) {
                    if (entity instanceof NSPCT5) {
                        entity.getDataWatcher().updateObject(NSPCT5.DATA_LENGTH, entity.getDataWatcher().getWatchableObjectInt(NSPCT5.DATA_LENGTH) < 5 ? entity.getDataWatcher().getWatchableObjectInt(NSPCT5.DATA_LENGTH) + 1 : 1);
                    }
                }
                else if (stack.getItem() instanceof ItemNSPCT5L) {
                    if (entity instanceof NSPCT5L) {
                        entity.getDataWatcher().updateObject(NSPCT5L.DATA_LENGTH, entity.getDataWatcher().getWatchableObjectInt(NSPCT5L.DATA_LENGTH) < 5 ? entity.getDataWatcher().getWatchableObjectInt(NSPCT5L.DATA_LENGTH) + 1 : 1);
                    }
                }

            }
        }
    }

}