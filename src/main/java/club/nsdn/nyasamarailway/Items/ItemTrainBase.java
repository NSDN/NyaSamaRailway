package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.Entity.MinecartBase;
import club.nsdn.nyasamarailway.Entity.NSBT1;
import club.nsdn.nyasamarailway.Entity.TrainBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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
                trainBody.onUpdate();

                if ((180.0 - trainBody.rotationYaw) % 180.0 == 0) {
                    bogieF.posX += 2.0;
                    bogieB.posX -= 2.0;
                } else if ((180.0 - trainBody.rotationYaw) % 180.0 != 0) {
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
