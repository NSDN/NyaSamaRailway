package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTrackPlate extends TileEntityBase {

    public static class TrackPlate extends TileEntity {
        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
        }
    }

    public TileEntityTrackPlate() {
        super("TrackPlate");
        setIconLocation("track_plate");
        setLightOpacity(0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TrackPlate();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

}
