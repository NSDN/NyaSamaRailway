package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class BiSignalLight extends AbsSignalLight {

    public static class TileEntityBiSignalLight extends club.nsdn.nyasamarailway.api.signal.TileEntityBiSignalLight {

        public TileEntityBiSignalLight() {
            setInfo(4, 0.3125, 0.6875, 0.25);
        }

    }

    public BiSignalLight() {
        super("BiSignalLight", "bi_signal_light");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityBiSignalLight();
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.DOWN;
    }

    @Override
    public void updateLight(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityBiSignalLight) {
            TileEntityBiSignalLight biSignalLight = (TileEntityBiSignalLight) tileEntity;
            boolean isEnable;
            if (biSignalLight.getSender() == null) {
                isEnable = biSignalLight.isPowered;
            } else {
                isEnable = biSignalLight.senderIsPowered();
            }
            int meta = biSignalLight.META;
            int old = meta;

            meta = meta & 0x3;
            if (biSignalLight.isBlinking) {
                if (biSignalLight.delay > 10) {
                    if (biSignalLight.delay < 20) {
                        biSignalLight.delay += 1;
                    } else {
                        biSignalLight.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, biSignalLight.lightType);
                } else {
                    biSignalLight.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, biSignalLight.lightType);
            }

            EnumFacing lightDir = getLightDir(world, pos);
            boolean isLightOn = isLightOn(world, pos);
            BlockLoader.lineLight.lightCtl(world, pos, lightDir, 8, isLightOn);

            if (old != meta || !biSignalLight.prevLightType.equals(biSignalLight.lightType)) {
                biSignalLight.prevLightType = biSignalLight.lightType;
                biSignalLight.META = meta;
                biSignalLight.refresh();
            }
        }
    }

}
