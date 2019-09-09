package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.rail.IBaseRail;
import club.nsdn.nyasamarailway.api.rail.IRailSwitch;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.9.9.
 */
public class RailBigBumper extends TileBlock {

    public static class TileEntityRailBigBumper extends TileEntityBase {

        public TileEntityRailBigBumper() {
            setInfo(4, 1, 1.5, 1);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

    }

    public RailBigBumper() {
        super("RailBigBumper");
        setRegistryName(NyaSamaRailway.MODID, "rail_big_bumper");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRailBigBumper();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.DOWN;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable() &&
                !(world.getBlockState(pos.down()).getBlock() instanceof IRailSwitch);
    }

    @Override
    @Nullable
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return Block.FULL_BLOCK_AABB;
        if (tileEntity instanceof TileEntityBase) {
            return ((TileEntityBase) tileEntity).AABB();
        }
        return Block.FULL_BLOCK_AABB;
    }

}
