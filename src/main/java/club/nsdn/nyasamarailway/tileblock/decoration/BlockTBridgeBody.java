package club.nsdn.nyasamarailway.tileblock.decoration;

/**
 * Created by drzzm32 on 2016.5.10.
 */

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTBridgeBody extends TileBlock {

    public static class TBridgeBody extends TileEntity {

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 16384.0D;
        }

    }

    public BlockTBridgeBody() {
        super("TBridgeBody");
        setIconLocation("t_bridge_body");
        setLightOpacity(0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TBridgeBody();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return true;
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        switch (meta % 13) {
            case 1:
                setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
                break;
            case 2:
                setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
                break;
            case 3:
                setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
                break;
            case 4:
                setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
                break;
            case 5:
                setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
                break;
            case 6:
                setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
                break;
            case 7:
                setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
                break;
            case 8:
                setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
                break;
            case 9:
                setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
                break;
            case 10:
                setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
                break;
            case 11:
                setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
                break;
            case 12:
                setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
                break;
        }
    }

}
