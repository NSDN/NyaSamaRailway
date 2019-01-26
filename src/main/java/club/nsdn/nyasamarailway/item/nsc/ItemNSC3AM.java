package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.nsc.NSC3AM;
import club.nsdn.nyasamarailway.util.ExtInfoCore;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.25.
 */
public class ItemNSC3AM extends ItemMinecart {

    public ItemNSC3AM() {
        super(-1);
        setUnlocalizedName("ItemNSC3AM");
        setTexName("item_nsc_3am");
        setMaxStackSize(64);
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    protected void setTexName(String name) {
        setTextureName("nyasamarailway" + ":" + name);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {
        if (BlockRailBase.func_150051_a(world.getBlock(x, y, z)))
        {
            if (!world.isRemote) {
                NSC3AM cart = new NSC3AM(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

                if (itemStack.hasDisplayName()) {
                    cart.setMinecartName(itemStack.getDisplayName());
                }

                ItemStack stack;
                for (int i = 0; i < 9; i++) {
                    stack = player.inventory.mainInventory[i];
                    if (stack == null) continue;
                    if (stack.getItem() == null) continue;
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
                        }
                    }
                }

                world.spawnEntityInWorld(cart);
            }

            --itemStack.stackSize;
            return true;
        } else {
            return false;
        }
    }
}
