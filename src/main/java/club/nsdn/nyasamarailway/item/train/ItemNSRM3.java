package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT5;
import club.nsdn.nyasamarailway.entity.loco.NSBT5M;
import club.nsdn.nyasamarailway.entity.train.NSRM3;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.3
 */
public class ItemNSRM3 extends AbsItemTrain {

    public ItemNSRM3() {
        super(NSRM3.class, "ItemNSRM3", "item_nsr_m3");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT5M motor = new NSBT5M(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM3 car = new NSRM3(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSBT5 bogie = new NSBT5(world, x + pos.getX(), y, z + pos.getZ());

        LinkageManager.INSTANCE.createLink(motor, bogie);
        car.setBogieA(motor).setBogieB(bogie);

        world.spawnEntity(motor); world.spawnEntity(bogie); world.spawnEntity(car);
    }

}
