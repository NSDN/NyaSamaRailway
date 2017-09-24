package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.Entity.LocoBase;
import club.nsdn.nyasamarailway.Entity.NSPCT6L;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.9.24.
 */
public class ItemNSPCT6L extends ItemMinecart {

    public ItemNSPCT6L() {
        super(-1);
        setUnlocalizedName("ItemNSPCT6L");
        setTexName("item_nspc_6l");
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
                LocoBase entityminecart = new NSPCT6L(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

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
