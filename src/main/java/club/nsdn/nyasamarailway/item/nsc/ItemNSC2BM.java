package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.entity.nsc.NSC2BM;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.24.
 */
public class ItemNSC2BM extends ItemMinecart {

    public ItemNSC2BM() {
        super(-1);
        setUnlocalizedName("ItemNSC2BM");
        setTexName("item_nsc_2bm");
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
                LocoBase entityminecart = new NSC2BM(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

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
