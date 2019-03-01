package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT3;
import club.nsdn.nyasamarailway.entity.train.NSRM2T;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.1
 */
public class ItemNSRM2T extends AbsItemTrain {

    public ItemNSRM2T() {
        super(NSRM2T.class, "ItemNSRM2T", "item_nsr_m2t");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT3 front = new NSBT3(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM2T car = new NSRM2T(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSBT3 back = new NSBT3(world, x + pos.getX(), y, z + pos.getZ());

        LinkageManager.INSTANCE.createLink(front, back);
        car.setBogieA(front).setBogieB(back);

        world.spawnEntity(front); world.spawnEntity(back); world.spawnEntity(car);
    }

}
