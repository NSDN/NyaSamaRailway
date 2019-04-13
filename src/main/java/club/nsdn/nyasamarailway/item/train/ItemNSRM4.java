package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSPCT8W;
import club.nsdn.nyasamarailway.entity.loco.NSPCT8C;
import club.nsdn.nyasamarailway.entity.train.NSRM4;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.20
 */
public class ItemNSRM4 extends AbsItemTrain {

    public ItemNSRM4() {
        super(NSRM4.class, "ItemNSRM4", "item_nsr_m4");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSPCT8C motor = new NSPCT8C(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM4 car = new NSRM4(world, x + pos.getX(), y - 3.5, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSPCT8W bogie = new NSPCT8W(world, x + pos.getX(), y, z + pos.getZ());

        bogie.setBogie(true);

        LinkageManager.INSTANCE.createLink(motor, bogie);
        car.setBogieA(motor).setBogieB(bogie);

        world.spawnEntity(motor); world.spawnEntity(bogie); world.spawnEntity(car);
    }

}
