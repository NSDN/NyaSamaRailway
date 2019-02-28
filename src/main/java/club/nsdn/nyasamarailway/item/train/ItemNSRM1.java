package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT2;
import club.nsdn.nyasamarailway.entity.loco.NSBT2M;
import club.nsdn.nyasamarailway.entity.train.NSRM1;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.2.27
 */
public class ItemNSRM1 extends AbsItemTrain {

    public ItemNSRM1() {
        super("ItemNSRM1", "item_nsr_m1");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT2M motor = new NSBT2M(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM1 car = new NSRM1(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSBT2 bogie = new NSBT2(world, x + pos.getX(), y, z + pos.getZ());

        LinkageManager.INSTANCE.createLink(motor, bogie);
        car.setBogieA(motor).setBogieB(bogie);

        world.spawnEntity(motor); world.spawnEntity(bogie); world.spawnEntity(car);
    }

}
