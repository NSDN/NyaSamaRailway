package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT3;
import club.nsdn.nyasamarailway.entity.loco.NSBT3M;
import club.nsdn.nyasamarailway.entity.train.NSRM2;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.1
 */
public class ItemNSRM2 extends AbsItemTrain {

    public ItemNSRM2() {
        super(NSRM2.class, "ItemNSRM2", "item_nsr_m2");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT3M motor = new NSBT3M(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM2 car = new NSRM2(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSBT3 bogie = new NSBT3(world, x + pos.getX(), y, z + pos.getZ());

        LinkageManager.INSTANCE.createLink(motor, bogie);
        car.setBogieA(motor).setBogieB(bogie);

        world.spawnEntity(motor); world.spawnEntity(bogie); world.spawnEntity(car);
    }

}
