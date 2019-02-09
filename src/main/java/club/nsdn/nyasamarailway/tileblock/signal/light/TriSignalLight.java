package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class TriSignalLight extends AbsSignalLight {

    public static class TileEntityTriSignalLight extends TileEntityTriStateReceiver {

        public TileEntityTriSignalLight() {
            setInfo(4, 0.3125, 1, 0.25);
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTriSignalLight();
    }

    public TriSignalLight() {
        super("TriSignalLight", "tri_signal_light");
    }

    @Override
    public void updateLight(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityTriSignalLight) {
            TileEntityTriSignalLight pillarSignal = (TileEntityTriSignalLight) tileEntity;
            int old = pillarSignal.META;
            int meta = old & 0x3;

            switch (pillarSignal.state) {
                case TileEntityTriSignalLight.STATE_POS: // Y
                    meta |= 0x4;
                    break;
                case TileEntityTriSignalLight.STATE_NEG: // R
                    meta |= 0x8;
                    break;
                case TileEntityTriSignalLight.STATE_ZERO: // G
                    meta &= 0x3;
                    break;
                default:
                    break;
            }

            pillarSignal.prevState = pillarSignal.state;
            pillarSignal.state = TileEntityTriSignalLight.STATE_ZERO;

            EnumFacing lightDir = getLightDir(world, pos);
            BlockLoader.lineLight.lightCtl(world, pos, lightDir, 8, true);

            if (old != meta) {
                pillarSignal.META = meta;
                pillarSignal.refresh();
            }
        }
    }
}
