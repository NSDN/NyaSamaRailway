package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT3G;
import club.nsdn.nyasamarailway.entity.loco.NSBT3GM;
import club.nsdn.nyasamarailway.entity.train.NSRM2G;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.9.9.
 */
public class ItemNSRM2G extends AbsItemTrain {

    public ItemNSRM2G() {
        super(NSRM2G.class, "ItemNSRM2G", "item_nsr_m2g");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT3GM motor = new NSBT3GM(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 4);
        NSRM2G car = new NSRM2G(world, x + pos.getX(), y + 2, z + pos.getZ());
        pos = pos.offset(facing, 4);
        NSBT3G bogie = new NSBT3G(world, x + pos.getX(), y, z + pos.getZ());

        LinkageManager.INSTANCE.createLink(motor, bogie);
        car.setBogieA(motor).setBogieB(bogie);

        world.spawnEntity(motor); world.spawnEntity(bogie); world.spawnEntity(car);
    }

}
