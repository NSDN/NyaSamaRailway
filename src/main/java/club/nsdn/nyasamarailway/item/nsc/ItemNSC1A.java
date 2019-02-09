package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.nsc.NSC1A;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import club.nsdn.nyasamarailway.util.ExtInfoCore;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.24.
 */
public class ItemNSC1A extends AbsItemCart {

    public ItemNSC1A() {
        super(NSC1A.class, "ItemNSC1A", "item_nsc_1a");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public boolean shouldSelfSpawn() {
        return true;
    }

    @Override
    public void selfSpawn(World world, double x, double y, double z, String name, EntityPlayer player) {
        NSC1A cart = new NSC1A(world, x, y, z);
        cart.setCustomNameTag(name);

        ItemStack stack;
        for (int i = 0; i < 9; i++) {
            stack = player.inventory.mainInventory.get(i);
            if (stack == ItemStack.EMPTY) continue;
            if (stack.getItem() instanceof NGTablet) {
                NBTTagList list = Util.getTagListFromNGT(stack);

                if (list != null) {
                    String[][] code = NSASM.getCode(list);

                    new ExtInfoCore(code) {
                        @Override
                        public World getWorld() {
                            return world;
                        }

                        @Override
                        public double getX() {
                            return x;
                        }

                        @Override
                        public double getY() {
                            return y;
                        }

                        @Override
                        public double getZ() {
                            return z;
                        }

                        @Override
                        public EntityPlayer getPlayer() {
                            return player;
                        }

                        @Override
                        public IExtendedInfoCart getCart() {
                            return cart;
                        }
                    }.run();

                    break;
                }
            }
        }

        world.spawnEntity(cart);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return null;
    }

}
