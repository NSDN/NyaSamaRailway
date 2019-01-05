package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityBiSignalLight;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2018.1.14.
 */
public class BlockPillarSignalBi extends AbsSignalLight {

    public static class BiSignalLight extends TileEntityBiSignalLight {

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

    public BlockPillarSignalBi() {
        super("PillarSignalBi");
        setIconLocation("pillar_signal_bi");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new BiSignalLight();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBoundsByXYZ(meta & 0x3, 0.25F, 0.15F, 0.5F, 0.75F, 0.85F, 1.0F);
    }

    public void updateLight(World world, int x ,int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof BiSignalLight) {
            BiSignalLight biSignalLight = (BiSignalLight) world.getTileEntity(x, y, z);
            boolean isEnable;
            if (biSignalLight.getSender() == null) {
                isEnable = biSignalLight.isPowered;
            } else {
                isEnable = biSignalLight.senderIsPowered();
            }
            int meta = world.getBlockMetadata(x, y, z);
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

            ForgeDirection lightDir = getLightDir(world, x, y, z);
            boolean isLightOn = isLightOn(world, x, y, z);
            BlockLoader.lineBeam.lightCtl(world, x, y, z, lightDir, 8, isLightOn);

            if (old != meta || !biSignalLight.prevLightType.equals(biSignalLight.lightType)) {
                biSignalLight.prevLightType = biSignalLight.lightType;
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

}
