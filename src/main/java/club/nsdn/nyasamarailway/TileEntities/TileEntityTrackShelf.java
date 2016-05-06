package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTrackShelf extends TileEntityBase {

    public static class TrackShelf extends TileEntity { }

    public TileEntityTrackShelf() {
        super("TrackShelf");
        setIconLocation("track_shelf");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TrackShelf();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

}
