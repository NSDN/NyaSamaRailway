package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityTrackSideBlocking;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.5.
 */
public class TrackSideBlocking extends AbsTrackSide {

    public static class Blocking extends TileEntityTrackSideBlocking {
    }

    public TrackSideBlocking() {
        super("TrackSideBlocking", "track_side_blocking");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new Blocking();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        TileEntityTrackSideBlocking.tick(world, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return false;
    }

}
