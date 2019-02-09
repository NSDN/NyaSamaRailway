package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.7.4.
 */
public class SignalLight extends AbsSignalLight {

    public static class TileEntitySignalLight extends club.nsdn.nyasamarailway.api.signal.TileEntitySignalLight {

        public TileEntitySignalLight() {
            setInfo(4, 1, 1, 1);
        }

    }

    public SignalLight() {
        super("SignalLight", "signal_light");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntitySignalLight();
    }

    @Override
    public void updateLight(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntitySignalLight) {
            TileEntitySignalLight signalLight = (TileEntitySignalLight) tileEntity;
            boolean isEnable;
            if (signalLight.getSender() == null) {
                isEnable = signalLight.isPowered;
            } else {
                isEnable = signalLight.senderIsPowered();
            }
            int meta = signalLight.META;
            int old = meta;

            meta = meta & 0x3;
            if (signalLight.isBlinking) {
                if (signalLight.delay > 10) {
                    if (signalLight.delay < 20) {
                        signalLight.delay += 1;
                    } else {
                        signalLight.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, signalLight.lightType);
                } else {
                    signalLight.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, signalLight.lightType);
            }

            EnumFacing lightDir = getLightDir(world, pos);
            boolean isLightOn = isLightOn(world, pos);
            BlockLoader.lineLight.lightCtl(world, pos, lightDir, 16, isLightOn);

            if (old != meta || !signalLight.prevLightType.equals(signalLight.lightType)) {
                signalLight.prevLightType = signalLight.lightType;
                signalLight.META = meta;
                signalLight.refresh();
            }
        }
    }

}
