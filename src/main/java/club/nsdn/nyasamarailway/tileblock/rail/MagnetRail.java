package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.api.rail.AbsRail;
import club.nsdn.nyasamarailway.api.rail.AbsRailBase;
import club.nsdn.nyasamarailway.api.rail.IBaseRail;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class MagnetRail extends AbsRail {

    public static class TileEntityMagnetRail extends TileEntityAbsRailBase implements IMonoRail {

        @Override
        public void update() { }

        @Override
        public int getMeta() {
            return getBlockMetadata();
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 8192.0;
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityMagnetRail();
    }

    public MagnetRail() {
        super("MagnetRail", "rail_mono_magnet");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 5.0F;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable();
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    public AxisAlignedBB getAscendingAABB() {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    @Override
    public AxisAlignedBB getFlatAABB() {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    @Nullable
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        AxisAlignedBB aabb = super.getBoundingBox(state, world, pos);
        if (world.getTileEntity(pos.down()) instanceof IBaseRail)
            aabb = aabb.expand(0, -0.25, 0);
        return aabb;
    }

    @Override
    public Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return MagnetRail.class;
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
