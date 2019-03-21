package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT6;
import club.nsdn.nyasamarailway.entity.loco.NSBT6M;
import club.nsdn.nyasamarailway.entity.train.NSRM5;
import club.nsdn.nyasamarailway.entity.train.NSRM5L;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.21
 */
public class ItemNSRM5L extends AbsItemTrain {

    public ItemNSRM5L() {
        super(NSRM5.class, "ItemNSRM5L", "item_nsr_m5l");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT6M motor = new NSBT6M(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 5);
        NSRM5L car = new NSRM5L(world, x + pos.getX(), y + 0.6875, z + pos.getZ());
        pos = pos.offset(facing, 5);
        NSBT6 bogie = new NSBT6(world, x + pos.getX(), y, z + pos.getZ());

        car.setBogieA(motor).setBogieB(bogie);
        motor.setLong(true); bogie.setLong(true);
        motor.setBaseCart(car); bogie.setBaseCart(car);
        LinkageManager.INSTANCE.createLink(motor, bogie);

        world.spawnEntity(motor); world.spawnEntity(bogie); world.spawnEntity(car);
    }

}
