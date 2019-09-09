package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT3G;
import club.nsdn.nyasamarailway.entity.train.NSRM2GT;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.9.9.
 */
public class ItemNSRM2GT extends AbsItemTrain {

    public ItemNSRM2GT() {
        super(NSRM2GT.class, "ItemNSRM2GT", "item_nsr_m2gt");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT3G front = new NSBT3G(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 4);
        NSRM2GT car = new NSRM2GT(world, x + pos.getX(), y + 2, z + pos.getZ());
        pos = pos.offset(facing, 4);
        NSBT3G back = new NSBT3G(world, x + pos.getX(), y, z + pos.getZ());

        LinkageManager.INSTANCE.createLink(front, back);
        car.setBogieA(front).setBogieB(back);

        world.spawnEntity(front); world.spawnEntity(back); world.spawnEntity(car);
    }

}
