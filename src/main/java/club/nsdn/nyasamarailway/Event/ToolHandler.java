package club.nsdn.nyasamarailway.Event;

import club.nsdn.nyasamarailway.Entity.ITrainLinkable;
import club.nsdn.nyasamarailway.ExtMod.ExRollerCoaster;
import club.nsdn.nyasamarailway.ExtMod.RailsOfWar;
import club.nsdn.nyasamarailway.Items.*;
import club.nsdn.nyasamarailway.TrainControl.NetworkWrapper;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
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
    public static Entity tmpLinkTrain = null;

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
                    if (entity instanceof EntityMinecart) {
                        controller8Bit = new TrainPacket(player.getEntityId(), entity.getEntityId(), 0, 5, 0);
                        NetworkWrapper.packetSender.sendTo(controller8Bit, player);
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
                        NetworkWrapper.packetSender.sendTo(controller32Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.cleared"));
                        return;
                    }
                    if (entity instanceof EntityMinecart) {
                        controller32Bit.cartID = entity.getEntityId();
                        if (!controller32Bit.trainUnits.contains(controller32Bit.cartID)) {
                            controller32Bit.trainUnits.add(controller32Bit.cartID);
                        }
                        controller32Bit.playerID = player.getEntityId();
                        NetworkWrapper.packetSender.sendTo(controller32Bit, player);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
                    } else {
                        if (ExRollerCoaster.getInstance().verifyEntity(entity) ||
                                RailsOfWar.getInstance().verifyEntity(entity)) {
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

                else if (stack.getItem() instanceof Item74HC04) {
                    if (entity instanceof ITrainLinkable) {
                        if (tmpLinkTrain == null) {
                            tmpLinkTrain = entity;
                            player.addChatComponentMessage(new ChatComponentTranslation("info.train.linking"));
                        } else {
                            if (((ITrainLinkable) tmpLinkTrain).getNextTrainID() == -1) {
                                ((ITrainLinkable) tmpLinkTrain).LinkTrain(entity.getEntityId());
                                player.addChatComponentMessage(new ChatComponentTranslation("info.train.linked"));
                            } else if (((ITrainLinkable) entity).getNextTrainID() == -1) {
                                ((ITrainLinkable) entity).LinkTrain(tmpLinkTrain.getEntityId());
                                player.addChatComponentMessage(new ChatComponentTranslation("info.train.linked"));
                            } else if (((ITrainLinkable) tmpLinkTrain).getNextTrainID() == entity.getEntityId()) {
                                ((ITrainLinkable) tmpLinkTrain).deLinkTrain(entity.getEntityId());
                                player.addChatComponentMessage(new ChatComponentTranslation("info.train.delinked"));
                            } else if (((ITrainLinkable) entity).getNextTrainID() == tmpLinkTrain.getEntityId()) {
                                ((ITrainLinkable) entity).deLinkTrain(tmpLinkTrain.getEntityId());
                                player.addChatComponentMessage(new ChatComponentTranslation("info.train.delinked"));
                            }
                            tmpLinkTrain = null;
                        }
                    }
                }

            }
        }
    }

}
