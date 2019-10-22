package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.nsc.*;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TrackSideReception extends AbsTrackSide {

    public static class TileEntityTrackSideReception extends club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideReception {

        public TileEntityTrackSideReception() {
            setInfo(13, 0.25, 0.3125, 1);
        }
        
        private EntityMinecart spawnNSC(World world, double x, double y, double z) {
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
                cart = EntityMinecart.create(world, x, y, z, EntityMinecart.Type.RIDEABLE);
            }
            return cart;
        }

        @Override
        public void spawn(World world, double x, double y, double z) {
            if (cartType.isEmpty()) return;
            if (cartType.equals("loco")) return;

            EntityMinecart cart = null;
            if (cartType.equals(NSPCT7.class.getName())) {
                cart = new NSPCT7(world, x, y, z);
            } else if (cartType.equals(NSPCT8.class.getName())) {
                cart = new NSPCT8(world, x, y, z);
                ((NSPCT8) cart).setExtendedInfo(extInfo);
            } else if (cartType.equals(NSPCT9.class.getName())) {
                cart = new NSPCT9(world, x, y, z);
                ((NSPCT9) cart).setExtendedInfo(extInfo);
            } else if (cartType.equals(NSPCT10.class.getName())) {
                cart = new NSPCT10(world, x, y, z);
                ((NSPCT10) cart).setExtendedInfo(extInfo);
            } else if (cartType.equals(NSPCT4.class.getName())) {
                cart = new NSPCT4(world, x, y, z);
                ((NSPCT4) cart).setExtendedInfo(extInfo);
            } else if (cartType.equals(NSPCT8W.class.getName())) {
                NSPCT8W.doSpawn(world, x, y, z);
            } else if (cartType.contains("nsc")) {
                spawnNSC(world, x, y, z);
            } else {
                cartType = "stock";
                cart = EntityMinecart.create(world, x, y, z, EntityMinecart.Type.RIDEABLE);
            }
            if (cart != null) world.spawnEntity(cart);
        }

    }

    public TrackSideReception() {
        super("TrackSideReception", "track_side_reception");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTrackSideReception();
    }

    @Override
    public boolean onConfigure(World world, BlockPos pos, EntityPlayer player) {
        return TileEntityTrackSideReception.configure(world, pos, player);
    }

}
