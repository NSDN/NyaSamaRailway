package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT4B;
import club.nsdn.nyasamarailway.entity.loco.NSBT4M;
import club.nsdn.nyasamarailway.entity.train.NSE4;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.3
 */
public class ItemNSE4 extends AbsItemTrain {

    public ItemNSE4() {
        super(NSE4.class, "ItemNSE4", "item_nse_4");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        BlockPos pos = new BlockPos(0, 0, 0);
        NSBT4M motor = new NSBT4M(world, x, y, z);
            pos = pos.offset(facing, 1);
            NSE4 car = new NSE4(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSBT4B bogie = new NSBT4B(world, x + pos.getX(), y, z + pos.getZ());

            LinkageManager.INSTANCE.createLink(motor, bogie);
            car.setBogieA(motor).setBogieB(bogie);

        world.spawnEntity(motor); world.spawnEntity(bogie); world.spawnEntity(car);
    }

}
