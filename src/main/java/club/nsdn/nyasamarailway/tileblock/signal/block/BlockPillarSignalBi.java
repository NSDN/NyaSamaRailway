package club.nsdn.nyasamarailway.tileblock.signal.block;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.tileblock.signal.AbsSignalLight;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityBiSignalLight;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2018.1.14.
 */
public class BlockPillarSignalBi extends AbsSignalLight {

    public static class BiSignalLight extends TileEntityBiSignalLight {
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
        float x = 1.0F, y = 1.0F, z = 1.0F;
        setBoundsByXYZ(meta & 0x3, 0.5F - x / 2, 0.0F, 0.5F - z / 2, 0.5F + x / 2, y, 0.5F + z / 2);
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
