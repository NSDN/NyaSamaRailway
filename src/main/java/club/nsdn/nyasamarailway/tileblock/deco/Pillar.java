package club.nsdn.nyasamarailway.tileblock.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.device.DeviceBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class Pillar extends TileBlock {

    public static class TileEntityPillar extends TileEntityBase {

        public TileEntityPillar() {

        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

    }

    public Pillar() {
        super("Pillar");
        setRegistryName(NyaSamaRailway.MODID, "pillar");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityPillar();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        doScanBlock(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor) {
        if (!world.isRemote) {
            doScanBlock(world, pos);
        }
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null)
            if (tileEntity instanceof TileEntityPillar) {
                TileEntityPillar pillar = (TileEntityPillar) tileEntity;
                int meta = pillar.META;

                AxisAlignedBB box = new AxisAlignedBB(
                        0.375, 0.375, 0.375,
                        0.625, 0.625, 0.625
                );

                if ((meta & Pillar.getValueByFacing(EnumFacing.UP)) != 0)
                    box = box.expand(0.0, 0.375, 0.0);
                if ((meta & Pillar.getValueByFacing(EnumFacing.DOWN)) != 0)
                    box = box.expand(0.0, -0.375, 0.0);
                if ((meta & Pillar.getValueByFacing(EnumFacing.NORTH)) != 0)
                    box = box.expand(0.0, 0.0, -0.375);
                if ((meta & Pillar.getValueByFacing(EnumFacing.SOUTH)) != 0)
                    box = box.expand(0.0, 0.0, 0.375);
                if ((meta & Pillar.getValueByFacing(EnumFacing.WEST)) != 0)
                    box = box.expand(-0.375, 0.0, 0.0);
                if ((meta & Pillar.getValueByFacing(EnumFacing.EAST)) != 0)
                    box = box.expand(0.375, 0.0, 0.0);

                return box;
            }
        return Block.FULL_BLOCK_AABB;
    }

    public boolean checkBlock(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof BlockAir) return false;
        if (block instanceof BlockPane) return false;
        if (block instanceof BlockStairs) return false;

        if (block instanceof BlockSlab) {
            if ((world.getBlockState(pos).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP))
                return world.getBlockState(pos.up()).getBlock() == this;
            else
                return world.getBlockState(pos.down()).getBlock() == this;
        }

        if (block instanceof BlockFence || block instanceof BlockWall) {
            if (world.getBlockState(pos.up()).getBlock() == this) return true;
            if (world.getBlockState(pos.down()).getBlock() == this) return true;
        }

        if (block instanceof DeviceBase) {
            if (world.getBlockState(pos.down()).getBlock() == this) return true;
        }

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            if (!(tileEntity instanceof TileEntityPillar)) {
                return world.getBlockState(pos.down()).getBlock() == this;
            }
        }

        Material material = state.getMaterial();
        if (material == Material.CLAY || material == Material.GROUND ||
            material == Material.IRON || material == Material.ROCK ||
            material == Material.GLASS || material == Material.SAND ||
            material == Material.WOOD
        ) return true;

        return material.isSolid();
    }

    public static int getValueByFacing(EnumFacing direction) {
        return 1 << (direction.ordinal());
    }

    public void doScanBlock(IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityPillar) {
            TileEntityPillar pillar = (TileEntityPillar) tileEntity;
            int meta = 0;

            if (checkBlock(world, pos.up())) meta |= getValueByFacing(EnumFacing.UP);
            if (checkBlock(world, pos.down())) meta |= getValueByFacing(EnumFacing.DOWN);
            if (checkBlock(world, pos.east())) meta |= getValueByFacing(EnumFacing.EAST);
            if (checkBlock(world, pos.west())) meta |= getValueByFacing(EnumFacing.WEST);
            if (checkBlock(world, pos.south())) meta |= getValueByFacing(EnumFacing.SOUTH);
            if (checkBlock(world, pos.north())) meta |= getValueByFacing(EnumFacing.NORTH);

            pillar.META = meta;
            pillar.refresh();
        }
    }

}
