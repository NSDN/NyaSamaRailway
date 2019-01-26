package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.entity.nsc.NSC3B;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.25.
 */
public class ItemNSC3B extends ItemMinecart {

    public ItemNSC3B() {
        super(-1);
        setUnlocalizedName("ItemNSC3B");
        setTexName("item_nsc_3b");
        setMaxStackSize(64);
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    protected void setTexName(String name) {
        setTextureName("nyasamarailway" + ":" + name);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {
        if (BlockRailBase.func_150051_a(world.getBlock(x, y, z)) && world.getBlock(x, y, z) instanceof RailMonoMagnetBase)
        {
            if (!world.isRemote)
            {
                MinecartBase entityminecart = new NSC3B(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

                if (itemStack.hasDisplayName())
                {
                    entityminecart.setMinecartName(itemStack.getDisplayName());
                }

                world.spawnEntityInWorld(entityminecart);
            }

            --itemStack.stackSize;
            return true;
        }
        else
        {
            return false;
        }
    }
}
