package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.api.signal.TileEntitySignalLight;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class PillarSignalOne extends AbsSignalLight {

    public static class TileEntityPillarSignalOne extends TileEntitySignalLight {

        public TileEntityPillarSignalOne() {
            setInfo(4, 0.5, 0.5, 0.5);
        }

        @Override
        protected void updateBounds() {
            setBoundsByXYZ(0.25, 0.25, 0.5, 0.75, 0.75, 1.0);
        }

    }

    public PillarSignalOne() {
        super("PillarSignalOne", "pillar_signal_one");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityPillarSignalOne();
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return false;
    }

    @Override
    public void updateLight(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityPillarSignalOne) {
            TileEntityPillarSignalOne pillarSignal = (TileEntityPillarSignalOne) tileEntity;
            boolean isEnable;
            if (pillarSignal.getSender() == null) {
                isEnable = pillarSignal.isPowered;
            } else {
                isEnable = pillarSignal.senderIsPowered();
            }
            int meta = pillarSignal.META;
            int old = meta;

            meta = meta & 0x3;
            if (pillarSignal.isBlinking) {
                if (pillarSignal.delay > 10) {
                    if (pillarSignal.delay < 20) {
                        pillarSignal.delay += 1;
                    } else {
                        pillarSignal.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, pillarSignal.lightType);
                } else {
                    pillarSignal.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, pillarSignal.lightType);
            }

            EnumFacing lightDir = getLightDir(world, pos);
            boolean isLightOn = isLightOn(world, pos);
            BlockLoader.lineLight.lightCtl(world, pos, lightDir, 16, isLightOn);

            if (old != meta || !pillarSignal.prevLightType.equals(pillarSignal.lightType)) {
                pillarSignal.prevLightType = pillarSignal.lightType;
                pillarSignal.META = meta;
                pillarSignal.refresh();
            }
        }
    }

}
