package club.nsdn.nyasamarailway.tileblock.signal.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.9.12.
 */
public class BlockGlassShield3X1D5 extends BlockGlassShield {

    public static class GlassShield extends BlockGlassShield.GlassShield { }

    public BlockGlassShield3X1D5() {
        super("GlassShield3X1D5", "glass_shield_3x1d5");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new GlassShield();
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = -0.5F, y1 = 0.0F, z1 = 0.4375F, x2 = 1.0F, y2 = 3.0F, z2 = 0.5625F;

        if ((meta & 0x8) != 0) x1 = 0.875F;
        switch (meta & 3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
    }

    @Override
    public GlassShield getNearbyShield(World world, int x, int y, int z) {
        if (world.getTileEntity(x + 1, y, z) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x + 1, y, z);
        }
        if (world.getTileEntity(x - 1, y, z) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x - 1, y, z);
        }
        if (world.getTileEntity(x, y, z + 1) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x, y, z + 1);
        }
        if (world.getTileEntity(x, y, z - 1) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x, y, z - 1);
        }
        return null;
    }

}
