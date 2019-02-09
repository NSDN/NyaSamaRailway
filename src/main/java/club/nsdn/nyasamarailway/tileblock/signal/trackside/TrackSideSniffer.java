package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.5.
 */
public class TrackSideSniffer extends AbsTrackSide {

    public static class TileEntityTrackSideSniffer extends club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideSniffer {

        public TileEntityTrackSideSniffer() {
            setInfo(13, 0.25, 0.3125, 1);
        }

    }

    public TrackSideSniffer() {
        super("TrackSideSniffer", "track_side_sniffer");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTrackSideSniffer();
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleUpdate(pos, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideSniffer.tick(world, pos);
    }

    @Override
    public boolean onConfigure(World world, BlockPos pos, EntityPlayer player) {
        return club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideSniffer.configure(world, pos, player);
    }

}
