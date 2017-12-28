package club.nsdn.nyasamarailway.item;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.entity.cart.NSBT1;
import club.nsdn.nyasamarailway.entity.TrainBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.23.
 */
public class ItemTrainBase extends ItemMinecart {

    public ItemTrainBase() {
        super(-1);
        setUnlocalizedName("ItemTrainBase");
        setTexName("item_nsft_1");
        setMaxStackSize(64);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public enum RailDirection {
        NONE,
        WE, //West-East
        NS //North-South
    }

    public RailDirection getRailDirection(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 2) == 0 && (meta & 4) == 0) {
            return ((meta & 1) == 0) ? RailDirection.NS : RailDirection.WE;
        } else if ((meta & 2) > 0 && (meta & 4) == 0) {
            return RailDirection.WE;
        } else if ((meta & 2) == 0 && (meta & 4) > 0) {
            return RailDirection.NS;
        }
        return RailDirection.NONE;
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
                TrainBase trainBody = new TrainBase(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                MinecartBase bogieF = new NSBT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                MinecartBase bogieB = new NSBT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);

                if (itemStack.hasDisplayName())
                {
                    trainBody.setMinecartName(itemStack.getDisplayName());
                }

                trainBody.addBogie(0, bogieF.getEntityId(), 2.0);
                trainBody.addBogie(1, bogieB.getEntityId(), -2.0);

                world.spawnEntityInWorld(trainBody);

                if (getRailDirection(world, x, y, z) == RailDirection.WE) {
                    bogieF.posX += 2.0;
                    bogieB.posX -= 2.0;
                } else if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                    bogieF.posZ += 2.0;
                    bogieB.posZ -= 2.0;
                }
                world.spawnEntityInWorld(bogieF);
                world.spawnEntityInWorld(bogieB);
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
