package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.22.
 */

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityRailSignHeadCut extends TileEntityBase {

    public static class RailSignHeadCut extends TileEntity {}

    public TileEntityRailSignHeadCut() {
        super("RailSignHeadCut");
        setIconLocation("rail_sign_head_cut");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new RailSignHeadCut();
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.0F, y1 = 0.0F, z1 = 0.25F, x2 = 1.0F, y2 = 1.0F, z2 = 0.75F;
        switch (meta % 13) {
            case 1:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 2:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 3:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 4:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
            case 5:
                setBlockBounds(x1, z1, y1, x2, z2, y2);
                break;
            case 6:
                setBlockBounds(1.0F - y2, z1, x1, 1.0F - y1, z2, x2);
                break;
            case 7:
                setBlockBounds(1.0F - x2, z1, 1.0F - y2, 1.0F - x1, z2, 1.0F - y1);
                break;
            case 8:
                setBlockBounds(y1, z1, 1.0F - x2, y2, z2, 1.0F - x1);
                break;
            case 9:
                setBlockBounds(x1, 1.0F - y2, z1, x2, 1.0F - y1, z2);
                break;
            case 10:
                setBlockBounds(1.0F - z2, 1.0F - y2, x1, 1.0F - z1, 1.0F - y1, x2);
                break;
            case 11:
                setBlockBounds(1.0F - x2, 1.0F - y2, 1.0F - z2, 1.0F - x1, 1.0F - y1, 1.0F - z1);
                break;
            case 12:
                setBlockBounds(z1, 1.0F - y2, 1.0F - x2, z2, 1.0F - y1, 1.0F - x1);
                break;
        }
    }

}
