package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.api.rail.AbsRail;
import club.nsdn.nyasamarailway.api.rail.AbsRailBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.9.9
 */
public class RailNorStoneSleeper extends AbsRail {

    public static class TileEntityRailNorStoneSleeper extends TileEntityAbsRailBase {
        @Override
        public double getMaxRenderDistanceSquared() {
            return 8192.0;
        }
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRailNorStoneSleeper();
    }

    public RailNorStoneSleeper() {
        super("RailNorStoneSleeper", "rail_nor");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 3.0F;
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
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
    }

    @Override
    public Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return RailNorStoneSleeper.class;
        }

        @Override
        public AbsRailBase.Rail getRail(World world, BlockPos pos, IBlockState state) {
            return new Rail(world, pos, state);
        }

        public Rail(World world, BlockPos pos, IBlockState state) {
            super(world, pos, state);
        }

    }

}
