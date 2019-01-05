package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2017.7.4.
 */
public class BlockSignalLight extends AbsSignalLight {

    public static class SignalLight extends club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight {
    }

    public BlockSignalLight() {
        super("SignalLight");
        setIconLocation("signal_light");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new SignalLight();
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void updateLight(World world, int x ,int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof SignalLight) {
            SignalLight signalLight = (SignalLight) world.getTileEntity(x, y, z);
            boolean isEnable;
            if (signalLight.getSender() == null) {
                isEnable = signalLight.isPowered ^ thisBlockIsPowered(world, x, y, z);
            } else {
                isEnable = signalLight.senderIsPowered() ^ thisBlockIsPowered(world, x, y, z);
            }
            int meta = world.getBlockMetadata(x, y, z);
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

            ForgeDirection lightDir = getLightDir(world, x, y, z);
            boolean isLightOn = isLightOn(world, x, y, z);
            BlockLoader.lineBeam.lightCtl(world, x, y, z, lightDir, 16, isLightOn);

            if (old != meta || !signalLight.prevLightType.equals(signalLight.lightType)) {
                signalLight.prevLightType = signalLight.lightType;
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

}
