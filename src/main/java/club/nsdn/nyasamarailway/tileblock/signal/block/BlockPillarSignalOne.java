package club.nsdn.nyasamarailway.tileblock.signal.block;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.tileblock.signal.AbsSignalLight;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.1.14.
 */
public class BlockPillarSignalOne extends AbsSignalLight {

    public static class SignalLight extends club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(2, 2, 2);
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 16384.0D;
        }

    }

    public BlockPillarSignalOne() {
        super("PillarSignalOne");
        setIconLocation("pillar_signal_one");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new SignalLight();
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBoundsByXYZ(meta & 0x3, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
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

            boolean isLightOn = isLightOn(world, x, y, z);
            BlockLoader.dotBeam.lightCtl(world, x, y, z, isLightOn);

            if (old != meta || !signalLight.prevLightType.equals(signalLight.lightType)) {
                signalLight.prevLightType = signalLight.lightType;
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

}
