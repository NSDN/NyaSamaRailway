package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.api.rail.AbsRail;
import club.nsdn.nyasamarailway.api.rail.AbsRailBase;
import club.nsdn.nyasamarailway.api.rail.IMonoSwitch;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class RailTriSwitch extends AbsRail {

    public static class TileEntityRailTriSwitch extends TileEntityTriStateReceiver implements IMonoSwitch {

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
        public double getMaxRenderDistanceSquared() {
            return 8192.0;
        }

        @Override
        public void update() {

        }

    }

    public static enum EnumState implements IStringSerializable {
        NEG("neg"),
        ZERO("zero"),
        POS("pos");

        public static EnumState fromInt(int v) {
            switch (v) {
                case -1: return NEG;
                case 0: return ZERO;
                case 1: return POS;
            }
            return ZERO;
        }

        private final String name;

        EnumState(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static final PropertyDirection DIRECTION = PropertyDirection.create("direction", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<EnumState> STATE = PropertyEnum.create("state", EnumState.class);

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SHAPE, DIRECTION, STATE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRailTriSwitch) {
            TileEntityRailTriSwitch triSwitch = (TileEntityRailTriSwitch) tileEntity;
            return super.getActualState(state, world, pos)
                    .withProperty(DIRECTION, triSwitch.direction)
                    .withProperty(STATE, EnumState.fromInt(triSwitch.prevState)); // for render
        }
        return super.getActualState(state, world, pos);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRailTriSwitch();
    }

    public RailTriSwitch() {
        super("RailTriSwitch", "rail_tri_switch");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 8.0F;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    public AxisAlignedBB getAscendingAABB() {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public AxisAlignedBB getFlatAABB() {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleUpdate(pos, this, 1);
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
        if (tileEntity instanceof TileEntityRailTriSwitch) {
            TileEntityRailTriSwitch triSwitch = (TileEntityRailTriSwitch) tileEntity;

            triSwitch.direction = getDirFromMeta(val).getOpposite();
            if ((val + 2) % 2 == 0) val = 0;
            else val = 1;

            Util.setStateWithTile(world, pos, getStateFromMeta(val));
        }
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            doSwitch(world, pos);
        }
    }

    public boolean railHasCart(World world, BlockPos pos) {
        float bBoxSize = 0.25F;
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                new AxisAlignedBB(
                        (double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize)
                )
        );
        return !bBox.isEmpty();
    }

    public int getDelayedPostTime() { return 3; }

    public void doSwitch(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRailTriSwitch) {
            TileEntityRailTriSwitch triSwitch = (TileEntityRailTriSwitch) tileEntity;
            int old = tileEntity.getBlockMetadata();
            int meta = 0;

            if (triSwitch.direction == null)
                triSwitch.direction = EnumFacing.DOWN;
            EnumFacing dir = triSwitch.direction;

            int oldState = triSwitch.state; boolean delayedPost = false;

            BlockPos vec = pos.offset(triSwitch.direction).subtract(pos);
            if (railHasCart(world, pos.add(vec.rotate(Rotation.COUNTERCLOCKWISE_90))))
                triSwitch.setStatePos();
            if (railHasCart(world, pos.add(vec.rotate(Rotation.CLOCKWISE_90))))
                triSwitch.setStateNeg();
            if (railHasCart(world, vec))
                triSwitch.state = TileEntityRailTriSwitch.STATE_ZERO;

            if (triSwitch.state != oldState) delayedPost = true;

            switch (triSwitch.state) {
                case TileEntityRailTriSwitch.STATE_POS: //left
                    switch (triSwitch.direction) {
                        case SOUTH: meta = 9; break;
                        case WEST: meta = 6; break;
                        case NORTH: meta = 7; break;
                        case EAST: meta = 8; break;
                    }
                    break;
                case TileEntityRailTriSwitch.STATE_NEG: //right
                    switch (triSwitch.direction) {
                        case SOUTH:  meta = 8; break;
                        case WEST: meta = 9; break;
                        case NORTH: meta = 6; break;
                        case EAST: meta = 7; break;
                    }
                    break;
                case TileEntityRailTriSwitch.STATE_ZERO:
                    switch (triSwitch.direction) {
                        case SOUTH: meta = 0; break;
                        case WEST: meta = 1; break;
                        case NORTH: meta = 0; break;
                        case EAST: meta = 1; break;
                    }
                    break;
            }
            triSwitch.prevState = triSwitch.state;
            triSwitch.state = TileEntityRailTriSwitch.STATE_ZERO;

            if (old != meta) {
                Util.setStateWithTile(world, pos, getStateFromMeta(meta));
                world.notifyNeighborsOfStateChange(pos, this, false);
                world.markBlockRangeForRenderUpdate(pos, pos);
            }

            world.scheduleUpdate(pos, this, delayedPost ? getDelayedPostTime() * 20 : 1);
        }
    }

    @Override
    public AbsRail.Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return MonoRailSwitch.class;
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
