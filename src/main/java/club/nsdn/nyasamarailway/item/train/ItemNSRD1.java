package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT2;
import club.nsdn.nyasamarailway.entity.cart.NSBT4A;
import club.nsdn.nyasamarailway.entity.cart.NSBT4B;
import club.nsdn.nyasamarailway.entity.loco.NSBT2M;
import club.nsdn.nyasamarailway.entity.train.NSRD1Main;
import club.nsdn.nyasamarailway.entity.train.NSRD1Shelf;
import club.nsdn.nyasamarailway.entity.train.NSRM1;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.1
 */
public class ItemNSRD1 extends AbsItemTrain {

    public ItemNSRD1() {
        super(NSRD1Main.class, "ItemNSRD1", "item_nsr_d1");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        NSBT4A bogieA1 = new NSBT4A(world, x, y, z);
        BlockPos pos = new BlockPos(0, 0, 0);
        pos = pos.offset(facing, 1);
        NSRD1Shelf shelfA = new NSRD1Shelf(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSBT4A bogieA2 = new NSBT4A(world, x + pos.getX(), y, z + pos.getZ());
        LinkageManager.INSTANCE.createLink(bogieA1, bogieA2);
        shelfA.setBogieA(bogieA1).setBogieB(bogieA2);

        pos = pos.offset(facing, 3);
        NSRD1Main main = new NSRD1Main(world, x + pos.getX(), y + 2, z + pos.getZ());

        pos = pos.offset(facing, 3);
        NSBT4B bogieB1 = new NSBT4B(world, x + pos.getX(), y, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSRD1Shelf shelfB = new NSRD1Shelf(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSBT4B bogieB2 = new NSBT4B(world, x + pos.getX(), y, z + pos.getZ());
        LinkageManager.INSTANCE.createLink(bogieB1, bogieB2);
        shelfB.setBogieA(bogieB1).setBogieB(bogieB2);

        LinkageManager.INSTANCE.createLink(bogieA2, bogieB1);
        main.setBogieA(shelfA).setBogieB(shelfB);

        world.spawnEntity(bogieA1); world.spawnEntity(shelfA); world.spawnEntity(bogieA2);
        world.spawnEntity(bogieB1); world.spawnEntity(shelfB); world.spawnEntity(bogieB2);
        world.spawnEntity(main);
    }

}
