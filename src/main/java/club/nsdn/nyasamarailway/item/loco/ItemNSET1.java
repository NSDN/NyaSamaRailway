package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.entity.loco.NSET1;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.6.23.
 */

public class ItemNSET1 extends ItemMinecart {

    public ItemNSET1() {
        super(-1);
        setUnlocalizedName("ItemNSET1");
        setTexName("item_nse_1");
        setMaxStackSize(64);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    protected void setTexName(String name) {
        setTextureName("nyasamarailway" + ":" + name);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {
        if (BlockRailBase.func_150051_a(world.getBlock(x, y, z)))
        {
            if (!world.isRemote)
            {
                LocoBase entityminecart = new NSET1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

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
