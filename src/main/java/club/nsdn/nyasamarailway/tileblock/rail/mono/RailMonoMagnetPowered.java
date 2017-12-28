package club.nsdn.nyasamarailway.tileblock.rail.mono;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

import java.util.List;

/**
 * Created by drzzm32 on 2017.1.13.
 */
public class RailMonoMagnetPowered extends RailMonoMagnetBase {

    public static class TileEntityRail extends TileEntity implements RailMonoMagnetPowerable {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    public RailMonoMagnetPowered() {
        super(true, "RailMonoMagnetPowered", "rail_mono_magnet_powered");
    }

    public RailMonoMagnetPowered(String name, String icon) {
        super(true, name, icon);
    }

    public boolean isRailPowered(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof RailMonoMagnetPowerable) {
            return (world.getBlockMetadata(x, y, z) & 8) > 0;
        }
        return false;
    }

    public int getRailChargeDistance() {
        return 32;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        float maxV = getRailMaxSpeed(world, cart, x, y, z);
        if (world.getBlockMetadata(x, y, z) >= 8) {
            if (Math.abs(cart.motionX) < maxV && Math.abs(cart.motionZ) < maxV) {
                cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 0.02);
            }
        } else {
            cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.2, 0.02);
            cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.2, 0.02);
        }
    }

    public void onRailPowered(World world, int x, int y, int z, int meta, boolean hasCart) {
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        int meta = world.getBlockMetadata(x, y, z);
        float bBoxSize = 0.125F;
        boolean hasCart = false;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize))
        );
        if (!bBox.isEmpty()) {
            hasCart = true;
        }
        if (meta >= 8) {
            onRailPowered(world, x, y, z, meta, hasCart);
        }
    }

    protected boolean func_150058_a(World world, int x, int y, int z, int meta, boolean bool, int r) {
        if (r >= getRailChargeDistance()) {
            return false;
        } else {
            int baseMeta = meta & 7;
            boolean var9 = true;
            switch (baseMeta) {
                case 0:
                    if (bool) {
                        ++z;
                    } else {
                        --z;
                    }
                    break;
                case 1:
                    if (bool) {
                        --x;
                    } else {
                        ++x;
                    }
                    break;
                case 2:
                    if (bool) {
                        --x;
                    } else {
                        ++x;
                        ++y;
                        var9 = false;
                    }

                    baseMeta = 1;
                    break;
                case 3:
                    if (bool) {
                        --x;
                        ++y;
                        var9 = false;
                    } else {
                        ++x;
                    }

                    baseMeta = 1;
                    break;
                case 4:
                    if (bool) {
                        ++z;
                    } else {
                        --z;
                        ++y;
                        var9 = false;
                    }

                    baseMeta = 0;
                    break;
                case 5:
                    if (bool) {
                        ++z;
                        ++y;
                        var9 = false;
                    } else {
                        --z;
                    }

                    baseMeta = 0;
            }

            return func_150057_a(world, x, y, z, bool, r, baseMeta) ? true : var9 && this.func_150057_a(world, x, y - 1, z, bool, r, baseMeta);
        }
    }

    protected boolean func_150057_a(World world, int x, int y, int z, boolean bool, int r, int prevBaseMeta)
    {
        Block block = world.getBlock(x, y, z);

        if ((block == this) || ((block instanceof RailMonoMagnetSignalTransfer)))
        {
            int meta = world.getBlockMetadata(x, y, z);
            int baseMeta = meta & 7;

            if (prevBaseMeta == 1 && (baseMeta == 0 || baseMeta == 4 || baseMeta == 5))
            {
                return false;
            }

            if (prevBaseMeta == 0 && (baseMeta == 1 || baseMeta == 2 || baseMeta == 3))
            {
                return false;
            }

            if ((meta & 8) != 0)
            {
                if (world.isBlockIndirectlyGettingPowered(x, y, z))
                {
                    return true;
                }

                return func_150058_a(world, x, y, z, meta, bool, r + 1);
            }
        }

        return false;
    }

    @Override
    protected void func_150048_a(World p_150048_1_, int p_150048_2_, int p_150048_3_, int p_150048_4_, int p_150048_5_, int p_150048_6_, Block p_150048_7_)
    {
        boolean flag = p_150048_1_.isBlockIndirectlyGettingPowered(p_150048_2_, p_150048_3_, p_150048_4_);
        flag = flag || this.func_150058_a(p_150048_1_, p_150048_2_, p_150048_3_, p_150048_4_, p_150048_5_, true, 0) || this.func_150058_a(p_150048_1_, p_150048_2_, p_150048_3_, p_150048_4_, p_150048_5_, false, 0);
        boolean flag1 = false;

        if (flag && (p_150048_5_ & 8) == 0)
        {
            p_150048_1_.setBlockMetadataWithNotify(p_150048_2_, p_150048_3_, p_150048_4_, p_150048_6_ | 8, 3);
            flag1 = true;
        }
        else if (!flag && (p_150048_5_ & 8) != 0)
        {
            p_150048_1_.setBlockMetadataWithNotify(p_150048_2_, p_150048_3_, p_150048_4_, p_150048_6_, 3);
            flag1 = true;
        }

        if (flag1)
        {
            p_150048_1_.notifyBlocksOfNeighborChange(p_150048_2_, p_150048_3_ - 1, p_150048_4_, this);

            if (p_150048_6_ == 2 || p_150048_6_ == 3 || p_150048_6_ == 4 || p_150048_6_ == 5)
            {
                p_150048_1_.notifyBlocksOfNeighborChange(p_150048_2_, p_150048_3_ + 1, p_150048_4_, this);
            }
        }
    }

}
