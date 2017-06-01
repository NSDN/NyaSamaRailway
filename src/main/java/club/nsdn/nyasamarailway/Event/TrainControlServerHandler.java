package club.nsdn.nyasamarailway.Event;

import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class TrainControlServerHandler {
    private static TrainControlServerHandler instance;


    public static TrainControlServerHandler instance() {
        if (instance == null)
            instance = new TrainControlServerHandler();
        return instance;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (ToolHandler.controller8Bit != null) {
            if (ToolHandler.controller8Bit.playerID > 0) {
                EntityPlayerMP player = ToolHandler.controller8Bit.getPlayerInServer();
                if (player != null) {
                    ItemStack stack = player.getHeldItemMainhand();
                    if (stack != null) {
                        if (stack.getItem() instanceof ItemTrainController8Bit) {
                            EntityMinecart cart = ToolHandler.controller8Bit.getCartInServer();
                            if (cart != null) {
                                TrainController.doMotion(ToolHandler.controller8Bit, cart);
                            }
                        }
                    }
                }
            }
        }

        if (ToolHandler.controller32Bit != null) {
            if (ToolHandler.controller32Bit.playerID > 0) {
                EntityPlayerMP player = ToolHandler.controller32Bit.getPlayerInServer();
                if (player != null) {
                    ItemStack stack = player.getHeldItemMainhand();
                    if (stack != null) {
                        if (stack.getItem() instanceof ItemTrainController32Bit) {
                            EntityMinecart cart;
                            if (!ToolHandler.controller32Bit.trainUnits.isEmpty()) {
                                for (int i : ToolHandler.controller32Bit.trainUnits) {
                                    cart = ToolHandler.controller32Bit.getCartInServer(i);
                                    if (cart != null) {
                                        TrainController.doMotion(ToolHandler.controller32Bit, cart);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
