package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.5.
 */
public class TrackSideRFID extends AbsTrackSide {

    public static class TileEntityTrackSideRFID extends club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideRFID {

        public TileEntityTrackSideRFID() {
            setInfo(13, 0.25, 0.3125, 1);
        }

    }

    public TrackSideRFID() {
        super("TrackSideRFID", "track_side_rfid");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTrackSideRFID();
    }


    @Override
    public boolean onConfigure(World world, BlockPos pos, EntityPlayer player) {
        return TileEntityTrackSideRFID.configure(world, pos, player);
    }

}
