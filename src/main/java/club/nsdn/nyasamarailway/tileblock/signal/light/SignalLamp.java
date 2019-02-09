package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.api.signal.TileEntitySignalLight;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.12.10.
 */
public class SignalLamp extends AbsSignalLight {

    public static class TileEntitySignalLamp extends TileEntitySignalLight {

        public TileEntitySignalLamp() {
            setInfo(4, 0.375, 1, 0.375);
        }

    }

    public SignalLamp() {
        super("SignalLamp", "signal_lamp");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntitySignalLamp();
    }

    @Override
    public void updateLight(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntitySignalLamp) {
            TileEntitySignalLamp signalLamp = (TileEntitySignalLamp) tileEntity;
            boolean isEnable;
            if (signalLamp.getSender() == null) {
                isEnable = signalLamp.isPowered;
            } else {
                isEnable = signalLamp.senderIsPowered();
            }
            int meta = signalLamp.META;
            int old = meta;

            meta = meta & 0x3;
            if (signalLamp.isBlinking) {
                if (signalLamp.delay > 10) {
                    if (signalLamp.delay < 20) {
                        signalLamp.delay += 1;
                    } else {
                        signalLamp.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, signalLamp.lightType);
                } else {
                    signalLamp.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, signalLamp.lightType);
            }

            boolean isLightOn = isLightOn(world, pos);
            BlockLoader.light.lightCtl(world, pos, isLightOn);

            if (old != meta || !signalLamp.prevLightType.equals(signalLamp.lightType)) {
                signalLamp.prevLightType = signalLamp.lightType;
                signalLamp.META = meta;
                signalLamp.refresh();
            }
        }
    }

}
