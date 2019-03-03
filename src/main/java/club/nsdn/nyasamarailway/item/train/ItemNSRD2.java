package club.nsdn.nyasamarailway.item.train;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT4A;
import club.nsdn.nyasamarailway.entity.cart.NSBT4B;
import club.nsdn.nyasamarailway.entity.train.NSRD1Main;
import club.nsdn.nyasamarailway.entity.train.NSRD1Shelf;
import club.nsdn.nyasamarailway.entity.train.NSRD2Main;
import club.nsdn.nyasamarailway.entity.train.NSRD2Shelf;
import club.nsdn.nyasamarailway.item.AbsItemTrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.thewdj.linkage.core.LinkageManager;

/**
 * Created by drzzm32 on 2019.3.3
 */
public class ItemNSRD2 extends AbsItemTrain {

    public ItemNSRD2() {
        super(NSRD1Main.class, "ItemNSRD2", "item_nsr_d2");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing) {
        BlockPos pos = new BlockPos(0, 0, 0);
        NSBT4A bogieA1 = new NSBT4A(world, x, y, z);
            pos = pos.offset(facing, 1);
            NSRD1Shelf shelfA1 = new NSRD1Shelf(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSBT4A bogieA2 = new NSBT4A(world, x + pos.getX(), y, z + pos.getZ());
            LinkageManager.INSTANCE.createLink(bogieA1, bogieA2);
            shelfA1.setBogieA(bogieA1).setBogieB(bogieA2);

                pos = pos.offset(facing, 1);
                NSRD2Shelf shelfA = new NSRD2Shelf(world, x + pos.getX(), y + 2, z + pos.getZ());

        pos = pos.offset(facing, 1);
        NSBT4A bogieA3 = new NSBT4A(world, x + pos.getX(), y, z + pos.getZ());
            pos = pos.offset(facing, 1);
            NSRD1Shelf shelfA2 = new NSRD1Shelf(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSBT4A bogieA4 = new NSBT4A(world, x + pos.getX(), y, z + pos.getZ(), 5.0F);
            LinkageManager.INSTANCE.createLink(bogieA3, bogieA4);
            shelfA2.setBogieA(bogieA3).setBogieB(bogieA4);

                LinkageManager.INSTANCE.createLink(bogieA2, bogieA3);
                shelfA.setBogieA(shelfA1).setBogieB(shelfA2);

                    pos = pos.offset(facing, 5);
                    NSRD2Main main = new NSRD2Main(world, x + pos.getX(), y + 2, z + pos.getZ());

        pos = pos.offset(facing, 5);
        NSBT4B bogieB1 = new NSBT4B(world, x + pos.getX(), y, z + pos.getZ(), 5.0F);
            pos = pos.offset(facing, 1);
            NSRD1Shelf shelfB1 = new NSRD1Shelf(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSBT4B bogieB2 = new NSBT4B(world, x + pos.getX(), y, z + pos.getZ());
            LinkageManager.INSTANCE.createLink(bogieB1, bogieB2);
            shelfB1.setBogieA(bogieB1).setBogieB(bogieB2);

                pos = pos.offset(facing, 1);
                NSRD2Shelf shelfB = new NSRD2Shelf(world, x + pos.getX(), y + 2, z + pos.getZ());

        pos = pos.offset(facing, 1);
        NSBT4B bogieB3 = new NSBT4B(world, x + pos.getX(), y, z + pos.getZ());
            pos = pos.offset(facing, 1);
            NSRD1Shelf shelfB2 = new NSRD1Shelf(world, x + pos.getX(), y + 1, z + pos.getZ());
        pos = pos.offset(facing, 1);
        NSBT4B bogieB4 = new NSBT4B(world, x + pos.getX(), y, z + pos.getZ());
            LinkageManager.INSTANCE.createLink(bogieB3, bogieB4);
            shelfB2.setBogieA(bogieB3).setBogieB(bogieB4);

                LinkageManager.INSTANCE.createLink(bogieB2, bogieB3);
                shelfB.setBogieA(shelfB1).setBogieB(shelfB2);

        LinkageManager.INSTANCE.createLink(bogieA4, bogieB1);
        main.setBogieA(shelfA).setBogieB(shelfB);

        world.spawnEntity(bogieA1); world.spawnEntity(shelfA1); world.spawnEntity(bogieA2);
        world.spawnEntity(bogieA3); world.spawnEntity(shelfA2); world.spawnEntity(bogieA4);
        world.spawnEntity(shelfA);
        world.spawnEntity(bogieB1); world.spawnEntity(shelfB1); world.spawnEntity(bogieB2);
        world.spawnEntity(bogieB3); world.spawnEntity(shelfB2); world.spawnEntity(bogieB4);
        world.spawnEntity(shelfB);
        world.spawnEntity(main);
    }

}
