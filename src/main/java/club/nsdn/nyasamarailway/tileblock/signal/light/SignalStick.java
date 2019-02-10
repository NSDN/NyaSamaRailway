package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.api.signal.TileEntitySignalLight;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class SignalStick extends AbsSignalLight {

    public static class TileEntitySignalStick extends TileEntitySignalLight {

        public TileEntitySignalStick() {
            setInfo(4, 0.25, 1, 0.25);
        }

    }

    public SignalStick() {
        super("SignalStick", "signal_stick");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntitySignalStick();
    }

    @Override
    public void updateLight(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntitySignalStick) {
            TileEntitySignalStick signalStick = (TileEntitySignalStick) tileEntity;
            boolean isEnable;
            if (signalStick.getSender() == null) {
                isEnable = signalStick.isPowered;
            } else {
                isEnable = signalStick.senderIsPowered();
            }
            int meta = signalStick.META;
            int old = meta;

            meta = meta & 0x3;
            if (signalStick.isBlinking) {
                if (signalStick.delay > 10) {
                    if (signalStick.delay < 20) {
                        signalStick.delay += 1;
                    } else {
                        signalStick.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, signalStick.lightType);
                } else {
                    signalStick.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, signalStick.lightType);
            }

            boolean isLightOn = isLightOn(world, pos);
            BlockLoader.light.lightCtl(world, pos, isLightOn);

            if (old != meta || !signalStick.prevLightType.equals(signalStick.lightType)) {
                signalStick.prevLightType = signalStick.lightType;
                signalStick.META = meta;
                signalStick.refresh();
            }
        }
    }

}
