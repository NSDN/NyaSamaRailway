package club.nsdn.nyasamarailway.tileblock.decoration;

/**
 * Created by drzzm32 on 2016.5.10.
 */

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTBridgeHeadNoRib extends TileBlock {

    public static class TBridgeHeadNoRib extends TileEntity {

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 16384.0D;
        }

    }

    public TileEntityTBridgeHeadNoRib() {
        super("TBridgeHeadNoRib");
        setIconLocation("t_bridge_head_no_rib");
        setLightOpacity(0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TBridgeHeadNoRib();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return true;
    }

}
