package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2016.5.9.
 */
public class BlockPlatform extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<EnumHalf> HALF = PropertyEnum.create("half", EnumHalf.class);

    protected static final AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.375D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_TALL = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public static enum EnumHalf implements IStringSerializable {
        TOP("top"),
        BOTTOM("bottom"),
        TALL("tall");

        private final String name;

        EnumHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

    public BlockPlatform() {
        super( Material.IRON);
        setUnlocalizedName("BlockPlatform");
        setRegistryName(NyaSamaRailway.MODID, "platform");
        setHardness(5.0F);
        setLightOpacity(255);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(HALF)) {
            case TOP: return AABB_TOP;
            case BOTTOM: return AABB_BOTTOM;
            case TALL: return AABB_TALL;
        }
        return Block.FULL_BLOCK_AABB;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return state.getValue(HALF) != EnumHalf.BOTTOM;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        EnumHalf half = EnumHalf.BOTTOM;
        switch ((meta >> 2) & 3) {
            case 1: half = EnumHalf.TOP; break;
            case 2: half = EnumHalf.TALL; break;
        }
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(HALF, half);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int half = 0;
        switch (state.getValue(HALF)) {
            case TOP: half = 1; break;
            case TALL: half = 2; break;
        }
        return state.getValue(FACING).getHorizontalIndex() | (half << 2);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HALF);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing facing) {
        if (facing.getAxis() == EnumFacing.Axis.Y && state.getValue(HALF) == EnumHalf.TALL) {
            return BlockFaceShape.SOLID;
        } else if (facing == EnumFacing.UP && state.getValue(HALF) == EnumHalf.TOP) {
            return BlockFaceShape.SOLID;
        } else {
            return facing == EnumFacing.DOWN && state.getValue(HALF) == EnumHalf.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        if (ForgeModContainer.disableStairSlabCulling) {
            return super.doesSideBlockRendering(state, world, pos, facing);
        } else {
            state = this.getActualState(state, world, pos);
            EnumFacing side = state.getValue(FACING);
            return facing == side;
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase player) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player).withProperty(HALF, EnumHalf.BOTTOM);
        if (world.getBlockState(pos.down()).getBlock() == this) {
            return state.withProperty(HALF, EnumHalf.TALL);
        } else {
            return facing == EnumFacing.DOWN || facing != EnumFacing.UP && (double)hitY > 0.5D ? state.withProperty(HALF, EnumHalf.TOP) : state;
        }
    }

}
