package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.entity.cart.*;
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

        @Override
        public void spawn(World world, int x, int y, int z) {
            TileEntityTrackSideReception reception = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideReception) {
                reception = (TileEntityTrackSideReception) world.getTileEntity(x, y, z);
            }
            if (reception != null) {
                if (reception.cartType.isEmpty()) return;
                if (reception.cartType.equals("loco")) return;

                if (reception.cartType.equals(NSTCT1.class.getName())) {
                    MinecartBase cart = new NSTCT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSBT1.class.getName())) {
                    MinecartBase cart = new NSBT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT1.class.getName())) {
                    MinecartBase cart = new NSPCT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT2.class.getName())) {
                    MinecartBase cart = new NSPCT2(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT3.class.getName())) {
                    MinecartBase cart = new NSPCT3(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT5L.class.getName())) {
                    MinecartBase cart = new NSPCT5L(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT6.class.getName())) {
                    MinecartBase cart = new NSPCT6(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT8.class.getName())) {
                    MinecartBase cart = new NSPCT8(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT10.class.getName())) {
                    MinecartBase cart = new NSPCT10(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (reception.cartType.equals(NSPCT9.class.getName())) {
                    NSPCT9 cart = new NSPCT9(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    cart.setExtendedInfo(reception.extInfo);
                    world.spawnEntityInWorld(cart);
                } else {
                    reception.cartType = "stock";
                    EntityMinecart cart = EntityMinecartEmpty.createMinecart(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, -1);
                    world.spawnEntityInWorld(cart);
                }
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
