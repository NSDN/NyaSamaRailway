package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT6;
import club.nsdn.nyasamarailway.entity.cart.NSPCT8W;
import club.nsdn.nyasamarailway.entity.train.NSRM5T;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.21
 */
public class ItemNSRM5T extends AbsItemTrain {

    public ItemNSRM5T() {
        super(NSRM5T.class, "ItemNSRM5T", "item_nsr_m5t");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT6 front = new NSBT6(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM5T car = new NSRM5T(world, x + pos.getX(), y + 0.6875, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSBT6 back = new NSBT6(world, x + pos.getX(), y, z + pos.getZ());

        car.setBogieA(front).setBogieB(back);
        front.setBaseCart(car); back.setBaseCart(car);
        LinkageManager.INSTANCE.createLink(front, back);

        world.spawnEntity(front); world.spawnEntity(back); world.spawnEntity(car);
    }

}
