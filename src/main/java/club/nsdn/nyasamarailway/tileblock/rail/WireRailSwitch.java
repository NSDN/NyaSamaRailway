package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.api.rail.*;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2020.3.24.
 */
public class WireRailSwitch extends AbsRail {

    public static class TileEntityWireRailSwitch extends TileEntityTriStateReceiver implements IRailSwitch {

        public EnumFacing direction;

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            direction = EnumFacing.byName(
                    tagCompound.getString("direction")
            );
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            if (direction == null) direction = EnumFacing.DOWN;
            tagCompound.setString("direction", direction.getName());

            return super.toNBT(tagCompound);
        }

        @Override
        public void update() {
            if (!world.isRemote)
                updateSignal(world, pos);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityWireRailSwitch) {
                TileEntityWireRailSwitch wireSwitch = (TileEntityWireRailSwitch) tileEntity;
                EnumFacing dir = wireSwitch.direction;
                int old = getBlockMetadata();

                int meta = 0;
                switch (wireSwitch.state) {
                    case TileEntityWireRailSwitch.STATE_POS: //left
                        switch (wireSwitch.direction) {
                            case SOUTH: meta = 9; break;
                            case WEST: meta = 6; break;
                            case NORTH: meta = 7; break;
                            case EAST: meta = 8; break;
                        }
                        break;
                    case TileEntityWireRailSwitch.STATE_NEG: //right
                        switch (wireSwitch.direction) {
                            case SOUTH:  meta = 8; break;
                            case WEST: meta = 9; break;
                            case NORTH: meta = 6; break;
                            case EAST: meta = 7; break;
                        }
                        break;
                    case TileEntityWireRailSwitch.STATE_ZERO:
                        switch (wireSwitch.direction) {
                            case SOUTH: meta = 0; break;
                            case WEST: meta = 1; break;
                            case NORTH: meta = 0; break;
                            case EAST: meta = 1; break;
                        }
                        break;
                }
                wireSwitch.state = TileEntityWireRailSwitch.STATE_ZERO;

                if (old != meta) {
                    Util.setStateWithTile(world, pos, getBlockType().getStateFromMeta(meta));
                    world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
                }
            }
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityWireRailSwitch();
    }

    public WireRailSwitch(String name, String id) {
        super(name, id);
    }

    public WireRailSwitch() {
        super("WireRailSwitch", "rail_wire_switch");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 2.0F;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    public AxisAlignedBB getAscendingAABB() {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public AxisAlignedBB getFlatAABB() {
        return new AxisAlignedBB(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor) {

    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing facing) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityWireRailSwitch) {
            TileEntityWireRailSwitch wireSwitch = (TileEntityWireRailSwitch) tileEntity;

            wireSwitch.direction = getDirFromMeta(val).getOpposite();
            if ((val + 2) % 2 == 0) val = 0;
            else val = 1;

            Util.setStateWithTile(world, pos, getStateFromMeta(val));
        }
    }

    @Override
    public AbsRail.Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return WireRailSwitch.class;
        }

        @Override
        public AbsRailBase.Rail getRail(World world, BlockPos pos, IBlockState state) {
            return new Rail(world, pos, state);
        }

        @Override
        public void setState(World world, BlockPos pos, IBlockState state) {

        }

        public Rail(World world, BlockPos pos, IBlockState state) {
            super(world, pos, state);
        }

    }

}
