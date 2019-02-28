package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT2;
import club.nsdn.nyasamarailway.entity.train.NSRM1T;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.1
 */
public class ItemNSRM1T extends AbsItemTrain {

    public ItemNSRM1T() {
        super(NSRM1T.class, "ItemNSRM1T", "item_nsr_m1t");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT2 front = new NSBT2(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM1T car = new NSRM1T(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSBT2 back = new NSBT2(world, x + pos.getX(), y, z + pos.getZ());

        LinkageManager.INSTANCE.createLink(front, back);
        car.setBogieA(front).setBogieB(back);

        world.spawnEntity(front); world.spawnEntity(back); world.spawnEntity(car);
    }

}
