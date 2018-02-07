package club.nsdn.nyasamarailway.tileblock.signal.block;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.tileblock.signal.AbsSignalLight;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityBiSignalLight;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class BlockBiSignalLight extends AbsSignalLight {

    public static class BiSignalLight extends TileEntityBiSignalLight {
    }

    public BlockBiSignalLight() {
        super("BiSignalLight");
        setIconLocation("bi_signal_light");
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
        float x1 = 0.34375F, y1 = 0.0F, z1 = 0.375F, x2 = 0.65625F, y2 = 0.6875F, z2 = 0.625F;

        switch (meta & 3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
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
