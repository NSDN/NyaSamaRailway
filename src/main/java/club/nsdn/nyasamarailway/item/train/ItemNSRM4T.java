package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSPCT8W;
import club.nsdn.nyasamarailway.entity.train.NSRM4T;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.20
 */
public class ItemNSRM4T extends AbsItemTrain {

    public ItemNSRM4T() {
        super(NSRM4T.class, "ItemNSRM4T", "item_nsr_m4t");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSPCT8W front = new NSPCT8W(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 2);
        NSRM4T car = new NSRM4T(world, x + pos.getX(), y - 3.5, z + pos.getZ());
        pos = pos.offset(facing, 2);
        NSPCT8W back = new NSPCT8W(world, x + pos.getX(), y, z + pos.getZ());

        front.setBogie(true); back.setBogie(true);

        LinkageManager.INSTANCE.createLink(front, back);
        car.setBogieA(front).setBogieB(back);

        world.spawnEntity(front); world.spawnEntity(back); world.spawnEntity(car);
    }

}
