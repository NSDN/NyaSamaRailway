package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.nsc.*;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityTrackSideReception;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.5.
 */
public class TrackSideReception extends AbsTrackSide {

    public static class Reception extends TileEntityTrackSideReception {
        
        private void spawnNSC(World world, double x, double y, double z) {
            EntityMinecart cart;
            if (cartType.equals(NSC1A.class.getName()))
                cart = new NSC1A(world, x, y, z);
            else if (cartType.equals(NSC1B.class.getName()))
                cart = new NSC1B(world, x, y, z);
            else if (cartType.equals(NSC2A.class.getName()))
                cart = new NSC2A(world, x, y, z);
            else if (cartType.equals(NSC2B.class.getName()))
                cart = new NSC2B(world, x, y, z);
            else if (cartType.equals(NSC3A.class.getName()))
                cart = new NSC3A(world, x, y, z);
            else if (cartType.equals(NSC3B.class.getName()))
                cart = new NSC3B(world, x, y, z);
            else {
                cartType = "stock";
                cart = EntityMinecartEmpty.createMinecart(world, x, y, z, -1);
            }
            world.spawnEntityInWorld(cart);
        }

        @Override
        public void spawn(World world, double x, double y, double z) {
            if (cartType.isEmpty()) return;
            if (cartType.equals("loco")) return;

            if (cartType.equals(NSTCT1.class.getName())) {
                MinecartBase cart = new NSTCT1(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSBT1.class.getName())) {
                MinecartBase cart = new NSBT1(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT1.class.getName())) {
                MinecartBase cart = new NSPCT1(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT2.class.getName())) {
                MinecartBase cart = new NSPCT2(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT3.class.getName())) {
                MinecartBase cart = new NSPCT3(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT5L.class.getName())) {
                MinecartBase cart = new NSPCT5L(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT6.class.getName())) {
                MinecartBase cart = new NSPCT6(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT8.class.getName())) {
                MinecartBase cart = new NSPCT8(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT10.class.getName())) {
                MinecartBase cart = new NSPCT10(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT4.class.getName())) {
                MinecartBase cart = new NSPCT4(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT5.class.getName())) {
                MinecartBase cart = new NSPCT5(world, x, y, z);
                world.spawnEntityInWorld(cart);
            } else if (cartType.equals(NSPCT6W.class.getName())) {
                NSPCT6W.doSpawn(world, x, y, z);
            } else if (cartType.equals(NSPCT8W.class.getName())) {
                NSPCT8W.doSpawn(world, x, y, z);
            } else if (cartType.equals(NSPCT9.class.getName())) {
                NSPCT9 cart = new NSPCT9(world, x, y, z);
                cart.setExtendedInfo(extInfo);
                world.spawnEntityInWorld(cart);
            } else if (cartType.contains("nsc")) {
                spawnNSC(world, x, y, z);
            } else {
                cartType = "stock";
                EntityMinecart cart = EntityMinecartEmpty.createMinecart(world, x, y, z, -1);
                world.spawnEntityInWorld(cart);
            }
        }

    }

    public TrackSideReception() {
        super("TrackSideReception", "track_side_reception");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new Reception();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        TileEntityTrackSideReception.tick(world, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return TileEntityTrackSideReception.configure(world, x, y, z, player);
    }

}
