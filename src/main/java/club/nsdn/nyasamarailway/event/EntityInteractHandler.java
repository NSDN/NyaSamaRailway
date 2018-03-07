package club.nsdn.nyasamarailway.event;

import club.nsdn.nyasamarailway.entity.cart.NSPCT5;
import club.nsdn.nyasamarailway.entity.cart.NSPCT5L;
import club.nsdn.nyasamarailway.entity.loco.NSPCT8J;
import club.nsdn.nyasamarailway.extmod.ExRollerCoaster;
import club.nsdn.nyasamarailway.extmod.RailsOfWar;
import club.nsdn.nyasamarailway.item.cart.ItemNSPCT5;
import club.nsdn.nyasamarailway.item.cart.ItemNSPCT5L;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

/**
 * Created by drzzm32 on 2016.6.7.
 */
public class EntityInteractHandler {
    private static EntityInteractHandler instance;

    public static EntityInteractHandler instance() {
        if (instance == null)
            instance = new EntityInteractHandler();
        return instance;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            if (player.worldObj.isRemote) return;
            Entity entity = event.target;

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {

                if (stack.getItem() instanceof ItemNTP8Bit) {
                    if (player.isSneaking()) {
                        ((ItemNTP8Bit) stack.getItem()).clearCart(stack, player);
                        return;
                    }
                    if (entity instanceof EntityMinecart) {
                        ((ItemNTP8Bit) stack.getItem()).addCart(stack, player, entity);
                    }
                }

                else if (stack.getItem() instanceof ItemNTP32Bit) {
                    if (player.isSneaking()) {
                        ((ItemNTP32Bit) stack.getItem()).clearCart(stack, player);
                        return;
                    }
                    if (entity instanceof EntityMinecart) {
                        ((ItemNTP32Bit) stack.getItem()).addCart(stack, player, entity);
                    } else {
                        if (ExRollerCoaster.getInstance() != null) {
                            if (ExRollerCoaster.getInstance().verifyEntity(entity)) {
                                ((ItemNTP32Bit) stack.getItem()).addCart(stack, player, entity);
                            }
                        }
                        if (RailsOfWar.getInstance() != null) {
                            if (RailsOfWar.getInstance().verifyEntity(entity)) {
                                ((ItemNTP32Bit) stack.getItem()).addCart(stack, player, entity);
                            }
                        }
                    }
                }

                else if (stack.getItem() instanceof Item1N4148) {
                    if (entity instanceof NSPCT8J) {
                        NSPCT8J loco = (NSPCT8J) entity;
                        loco.modifyHighSpeedMode(player);
                    }
                }

                else if (stack.getItem() instanceof ItemNSPCT5) {
                    if (entity instanceof NSPCT5) {
                        ((NSPCT5) entity).modifyLength();
                    }
                }
                else if (stack.getItem() instanceof ItemNSPCT5L) {
                    if (entity instanceof NSPCT5L) {
                        ((NSPCT5L) entity).modifyLength();
                    }
                }

            }
        }
    }

}
