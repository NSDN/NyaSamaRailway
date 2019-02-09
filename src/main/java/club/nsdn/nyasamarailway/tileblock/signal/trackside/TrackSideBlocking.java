package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.5.
 */
public class TrackSideBlocking extends AbsTrackSide {

    public static class TileEntityTrackSideBlocking extends club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideBlocking {

        public TileEntityTrackSideBlocking() {
            setInfo(13, 0.25, 0.3125, 1);
        }

    }

    public TrackSideBlocking() {
        super("TrackSideBlocking", "track_side_blocking");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTrackSideBlocking();
    }

    @Override
    public boolean onConfigure(World world, BlockPos pos, EntityPlayer player) {
        return false;
    }

}
