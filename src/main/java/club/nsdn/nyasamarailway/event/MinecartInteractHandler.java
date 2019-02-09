package club.nsdn.nyasamarailway.event;

import club.nsdn.nyasamarailway.api.cart.IHighSpeedCart;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by drzzm32 on 2016.6.7.
 */
public class MinecartInteractHandler {
    private static MinecartInteractHandler instance;

    public static MinecartInteractHandler instance() {
        if (instance == null)
            instance = new MinecartInteractHandler();
        return instance;
    }

    @SubscribeEvent
    public void onEntityInteract(MinecartInteractEvent event) {
        if (event.getPlayer() != null) {
            EntityPlayer player = event.getPlayer();
            if (player.world.isRemote) return;
            EntityMinecart minecart = event.getMinecart();

            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {

                if (stack.getItem() instanceof ItemNTP8Bit) {
                    if (player.isSneaking()) {
                        ((ItemNTP8Bit) stack.getItem()).clearCart(stack, player);
                        return;
                    }
                    ((ItemNTP8Bit) stack.getItem()).addCart(stack, player, minecart);
                }

                else if (stack.getItem() instanceof ItemNTP32Bit) {
                    if (player.isSneaking()) {
                        ((ItemNTP32Bit) stack.getItem()).clearCart(stack, player);
                        return;
                    }
                    ((ItemNTP32Bit) stack.getItem()).addCart(stack, player, minecart);
                }

                else if (stack.getItem() instanceof Item1N4148) {
                    if (minecart instanceof IHighSpeedCart) {
                        IHighSpeedCart highSpeedCart = (IHighSpeedCart) minecart;
                        highSpeedCart.modifyHighSpeedMode(player);
                    }
                }

            }
        }
    }

}
