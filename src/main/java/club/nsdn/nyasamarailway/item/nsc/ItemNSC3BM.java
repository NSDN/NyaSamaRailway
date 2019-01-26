package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.entity.nsc.NSC3BM;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.24.
 */
public class ItemNSC3BM extends ItemMinecart {

    public ItemNSC3BM() {
        super(-1);
        setUnlocalizedName("ItemNSC3BM");
        setTexName("item_nsc_3bm");
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
                LocoBase entityminecart = new NSC3BM(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

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
