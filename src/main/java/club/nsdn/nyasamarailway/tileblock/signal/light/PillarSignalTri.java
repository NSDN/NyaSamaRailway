package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class PillarSignalTri extends AbsSignalLight {

    public static class TileEntityPillarSignalTri extends TileEntityTriStateReceiver {

        public TileEntityPillarSignalTri() {
            setInfo(4, 0.5, 0.8, 0.5);
        }

        @Override
        protected void updateBounds() {
            setBoundsByXYZ(0.25F, 0.1F, 0.5F, 0.75F, 0.9F, 1.0F);
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityPillarSignalTri();
    }

    public PillarSignalTri() {
        super("PillarSignalTri", "pillar_signal_tri");
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return false;
    }

    @Override
    public void updateLight(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityPillarSignalTri) {
            TileEntityPillarSignalTri pillarSignal = (TileEntityPillarSignalTri) tileEntity;
            int old = pillarSignal.META;
            int meta = old & 0x3;

            switch (pillarSignal.state) {
                case TileEntityPillarSignalTri.STATE_POS: // Y
                    meta |= 0x4;
                    break;
                case TileEntityPillarSignalTri.STATE_NEG: // R
                    meta |= 0x8;
                    break;
                case TileEntityPillarSignalTri.STATE_ZERO: // G
                    meta &= 0x3;
                    break;
                default:
                    break;
            }

            pillarSignal.prevState = pillarSignal.state;
            pillarSignal.state = TileEntityPillarSignalTri.STATE_ZERO;

            EnumFacing lightDir = getLightDir(world, pos);
            BlockLoader.lineLight.lightCtl(world, pos, lightDir, 16, true);

            if (old != meta) {
                pillarSignal.META = meta;
                pillarSignal.refresh();
            }
        }
    }

}
