package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.rail.AbsRail;
import club.nsdn.nyasamarailway.api.rail.AbsRailBase;
import club.nsdn.nyasamarailway.api.rail.IConvWireMono;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2018.2.21.
 */
public class ConvWireMono extends AbsRail {

    public static class TileEntityConvWireMono extends TileEntityBase implements IConvWireMono {

        public EnumFacing direction;

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
        public void update() { }

        @Override
        public EnumFacing getDirection() {
            return direction;
        }

        public static void switchState(World world, BlockPos pos) {
            IBlockState nowState = world.getBlockState(pos);
            IBlockState defState = nowState.getBlock().getDefaultState();
            IBlockState newState = defState;
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityConvWireMono) {
                TileEntityConvWireMono conv = (TileEntityConvWireMono) tileEntity;

                switch (conv.direction) {
                    case NORTH:
                        if (nowState.getValue(SHAPE) == EnumRailDirection.NORTH_SOUTH)
                            newState = defState.withProperty(SHAPE, EnumRailDirection.ASCENDING_SOUTH);
                        else newState = defState.withProperty(SHAPE, EnumRailDirection.NORTH_SOUTH);
                        break;
                    case SOUTH:
                        if (nowState.getValue(SHAPE) == EnumRailDirection.NORTH_SOUTH)
                            newState = defState.withProperty(SHAPE, EnumRailDirection.ASCENDING_NORTH);
                        else newState = defState.withProperty(SHAPE, EnumRailDirection.NORTH_SOUTH);
                        break;
                    case WEST:
                        if (nowState.getValue(SHAPE) == EnumRailDirection.EAST_WEST)
                            newState = defState.withProperty(SHAPE, EnumRailDirection.ASCENDING_EAST);
                        else newState = defState.withProperty(SHAPE, EnumRailDirection.EAST_WEST);
                        break;
                    case EAST:
                        if (nowState.getValue(SHAPE) == EnumRailDirection.EAST_WEST)
                            newState = defState.withProperty(SHAPE, EnumRailDirection.ASCENDING_WEST);
                        else newState = defState.withProperty(SHAPE, EnumRailDirection.EAST_WEST);
                        break;
                }

                Util.setStateWithTile(world, pos, newState);
            }
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityConvWireMono();
    }

    public ConvWireMono() {
        super("ConvWireMono", "conv_wire_mono");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 2.0F;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable();
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
        if (tileEntity instanceof TileEntityConvWireMono) {
            TileEntityConvWireMono conv = (TileEntityConvWireMono) tileEntity;

            conv.direction = getDirFromMeta(val).getOpposite();
            if ((val + 2) % 2 == 0) val = 0;
            else val = 1;

            Util.setStateWithTile(world, pos, getStateFromMeta(val));
        }
    }

    @Override
    public AxisAlignedBB getAscendingAABB() {
        return Block.FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getFlatAABB() {
        return Block.FULL_BLOCK_AABB;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    public Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return ConvWireMono.class;
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
