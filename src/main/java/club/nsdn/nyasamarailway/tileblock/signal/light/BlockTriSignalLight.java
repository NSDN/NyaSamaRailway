package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class BlockTriSignalLight extends AbsSignalLight {

    public static class TriSignalLight extends TileEntityTriStateReceiver {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 16384.0D;
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TriSignalLight();
    }

    public BlockTriSignalLight() {
        super("TriSignalLight");
        setIconLocation("tri_signal_light");
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.34375F, y1 = 0.0F, z1 = 0.375F, x2 = 0.65625F, y2 = 1.0F, z2 = 0.625F;

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

    public void updateLight(World world, int x , int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof TriSignalLight) {
            TriSignalLight triSignalLight = (TriSignalLight) world.getTileEntity(x, y, z);
            int old = world.getBlockMetadata(x, y, z);
            int meta = old & 0x3;

            switch (triSignalLight.state) {
                case TriSignalLight.STATE_POS: // Y
                    meta |= 0x4;
                    break;
                case TriSignalLight.STATE_NEG: // R
                    meta |= 0x8;
                    break;
                case TriSignalLight.STATE_ZERO: // G
                    meta &= 0x3;
                    break;
                default:
                    break;
            }

            triSignalLight.prevState = triSignalLight.state;
            triSignalLight.state = TriSignalLight.STATE_ZERO;

            ForgeDirection lightDir = getLightDir(world, x, y, z);
            BlockLoader.lineBeam.lightCtl(world, x, y, z, lightDir, 8, true);

            if (old != meta) {
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.notifyBlockChange(x, y, z, this);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }
}
