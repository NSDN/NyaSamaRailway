package club.nsdn.nyasamarailway.item.cart;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.cart.NSPCT9;
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
 * Created by drzzm32 on 2018.3.21.
 */
public class ItemNSPCT9 extends AbsItemCart {

    public ItemNSPCT9() {
        super(NSPCT9.class, "ItemNSPCT9", "item_nspc_9");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public boolean shouldSelfSpawn() {
        return true;
    }

    @Override
    public void selfSpawn(World world, double x, double y, double z, String name, EntityPlayer player) {
        NSPCT9 cart = new NSPCT9(world, x, y, z);
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
