package club.nsdn.nyasamarailway.tileblock.signal.block;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.tileblock.signal.AbsSignalLight;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2018.1.14.
 */
public class BlockPillarSignalTri extends AbsSignalLight {

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

    public BlockPillarSignalTri() {
        super("PillarSignalTri");
        setIconLocation("pillar_signal_tri");
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x = 1.0F, y = 1.0F, z = 1.0F;
        setBoundsByXYZ(meta & 0x3, 0.5F - x / 2, 0.0F, 0.5F - z / 2, 0.5F + x / 2, y, 0.5F + z / 2);
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
