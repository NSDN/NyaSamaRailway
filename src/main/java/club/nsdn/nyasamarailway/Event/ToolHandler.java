package club.nsdn.nyasamarailway.Event;

import club.nsdn.nyasamarailway.Items.*;
import club.nsdn.nyasamarailway.TrainControl.NetworkWrapper;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by drzzm32 on 2017.5.21.
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
    public void onEntityInteract(PlayerInteractEvent event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            Entity entity = event.getEntity();

            ItemStack stack = player.getHeldItemMainhand();
            if (stack != null) {

                if (stack.getItem() instanceof ItemTrainController8Bit) {
                    if (player.isSneaking()) {
                        controller8Bit = new TrainPacket();
                        NetworkWrapper.packetSender.sendTo(ToolHandler.controller8Bit, player);
                        player.addChatComponentMessage(new TextComponentTranslation("info.ntp.cleared"));
                        return;
                    }
                    if (entity instanceof EntityMinecart) {
                        controller8Bit = new TrainPacket(player.getEntityId(), entity.getEntityId(), 0, 5, 0);
                        NetworkWrapper.packetSender.sendTo(controller8Bit, player);
                        player.addChatComponentMessage(new TextComponentTranslation("info.ntp.controlled"));
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
                        player.addChatComponentMessage(new TextComponentTranslation("info.ntp.cleared"));
                        return;
                    }
                    if (entity instanceof EntityMinecart) {
                        controller32Bit.cartID = entity.getEntityId();
                        if (!controller32Bit.trainUnits.contains(controller32Bit.cartID)) {
                            controller32Bit.trainUnits.add(controller32Bit.cartID);
                        }
                        controller32Bit.playerID = player.getEntityId();
                        NetworkWrapper.packetSender.sendTo(controller32Bit, player);
                        player.addChatComponentMessage(new TextComponentTranslation("info.ntp.controlled"));
                    }
                }
            }
        }
    }

}
