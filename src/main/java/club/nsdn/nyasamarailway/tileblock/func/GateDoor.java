package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class GateDoor extends TileBlock {

    public static class TileEntityGateDoor extends TileEntityBase {

        public int progress = 0;
        public float prevDist;

        public static final int PROGRESS_MAX = 10;

        public static final int STATE_CLOSE = 0;
        public static final int STATE_CLOSING = 1;
        public static final int STATE_OPEN = 2;
        public static final int STATE_OPENING = 3;
        public int state = STATE_CLOSE;

        public TileEntityGateDoor() {
            setInfo(4, 1.5, 1.5, 0.125);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            progress = tagCompound.getInteger("progress");
            state = tagCompound.getInteger("state");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("progress", progress);
            tagCompound.setInteger("state", state);

            return super.toNBT(tagCompound);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGateDoor) {
                TileEntityGateDoor gateDoor = (TileEntityGateDoor) tileEntity;

                switch (gateDoor.state) {
                    case TileEntityGateDoor.STATE_CLOSE:
                        if ((gateDoor.META & 0x8) != 0) {
                            gateDoor.META &= 0x7;
                            gateDoor.refresh();
                        }
                        break;
                    case TileEntityGateDoor.STATE_CLOSING:
                        if (gateDoor.progress > 0) gateDoor.progress -= 1;
                        else {
                            gateDoor.state = TileEntityGateDoor.STATE_CLOSE;
                            gateDoor.META &= 0x7;
                            gateDoor.refresh();
                        }
                        break;
                    case TileEntityGateDoor.STATE_OPEN:
                        if ((gateDoor.META & 0x8) == 0) {
                            gateDoor.META |= 0x8;
                            gateDoor.refresh();
                        }
                        break;
                    case TileEntityGateDoor.STATE_OPENING:
                        if (gateDoor.progress < TileEntityGateDoor.PROGRESS_MAX)
                            gateDoor.progress += 1;
                        else {
                            gateDoor.state = TileEntityGateDoor.STATE_OPEN;
                            gateDoor.META |= 0x8;
                            gateDoor.refresh();
                        }
                        break;
                    default:
                        break;
                }

                if (
                    gateDoor.state == TileEntityGateDoor.STATE_OPENING ||
                    gateDoor.state == TileEntityGateDoor.STATE_CLOSING
                ) {
                    gateDoor.refresh();
                }
            }
        }
    }

    public GateDoor() {
        super("GateDoor");
        setRegistryName(NyaSamaRailway.MODID, "gate_door");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGateDoor();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            if (facing.getAxis() == EnumFacing.Axis.Y) return false;
            return dir == facing.rotateY() || dir == facing.rotateY().getOpposite();
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return Block.FULL_BLOCK_AABB;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            if ((meta & 0x8) != 0) {
                return Block.NULL_AABB;
            }
            return ((TileEntityBase) tileEntity).AABB();
        }
        return Block.FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        AxisAlignedBB aabb = super.getSelectedBoundingBox(state, world, pos);
        return aabb.contract(0, 0.625, 0)
                .expand(0, -0.125, 0);
    }

}
