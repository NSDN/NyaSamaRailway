package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.entity.loco.NSPCT4M;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.12.1.
 */
public class ItemNSPCT4M extends ItemMinecart {

    public ItemNSPCT4M() {
        super(-1);
        setUnlocalizedName("ItemNSPCT4M");
        setTexName("item_nspc_4m");
        setMaxStackSize(64);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
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
                LocoBase entityminecart = new NSPCT4M(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

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
