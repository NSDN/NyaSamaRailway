package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class BlockPlatform extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<EnumHalf> HALF = PropertyEnum.create("half", EnumHalf.class);

    protected static final AxisAlignedBB AABB_NORTH_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB AABB_NORTH_TOP = new AxisAlignedBB(0.0D, 0.5D, 0.375D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_NORTH_TALL = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 0.625D);
    protected static final AxisAlignedBB AABB_SOUTH_TOP = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 0.625D);
    protected static final AxisAlignedBB AABB_SOUTH_TALL = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D);
    protected static final AxisAlignedBB AABB_WEST_BOTTOM = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB AABB_WEST_TOP = new AxisAlignedBB(0.375D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_WEST_TALL = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_EAST_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 0.5D, 1.0D);
    protected static final AxisAlignedBB AABB_EAST_TOP = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_EAST_TALL = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);

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
        setLightOpacity(1);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case NORTH:
                switch (state.getValue(HALF)) {
                    case TOP: return AABB_NORTH_TOP;
                    case BOTTOM: return AABB_NORTH_BOTTOM;
                    case TALL: return AABB_NORTH_TALL;
                }
                break;
            case SOUTH:
                switch (state.getValue(HALF)) {
                    case TOP: return AABB_SOUTH_TOP;
                    case BOTTOM: return AABB_SOUTH_BOTTOM;
                    case TALL: return AABB_SOUTH_TALL;
                }
                break;
            case WEST:
                switch (state.getValue(HALF)) {
                    case TOP: return AABB_WEST_TOP;
                    case BOTTOM: return AABB_WEST_BOTTOM;
                    case TALL: return AABB_WEST_TALL;
                }
                break;
            case EAST:
                switch (state.getValue(HALF)) {
                    case TOP: return AABB_EAST_TOP;
                    case BOTTOM: return AABB_EAST_BOTTOM;
                    case TALL: return AABB_EAST_TALL;
                }
                break;
        }
        return Block.FULL_BLOCK_AABB;
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
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        if (state.getValue(HALF) == EnumHalf.TALL && (facing.getAxis() == EnumFacing.Axis.Y || facing.getOpposite() == state.getValue(FACING))) {
            return true;
        } else if (facing == EnumFacing.UP && state.getValue(HALF) == EnumHalf.TOP) {
            return true;
        } else {
            return facing == EnumFacing.DOWN && state.getValue(HALF) == EnumHalf.BOTTOM;
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase player) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player);
        state = state.withProperty(FACING, player.getHorizontalFacing().getOpposite()).withProperty(HALF, EnumHalf.BOTTOM);
        return facing == EnumFacing.DOWN || facing != EnumFacing.UP && hitY > 0.5 ? state.withProperty(HALF, EnumHalf.TOP) : state;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        if (!world.isRemote) {
            if (player.getHeldItemMainhand().getItem() == ItemBlock.getItemFromBlock(this)) {
                if (facing.getAxis() == EnumFacing.Axis.Y && hitY >= 0.5) {
                    state = world.getBlockState(pos).withProperty(HALF, EnumHalf.TALL);
                    world.setBlockState(pos, state);
                    world.markBlockRangeForRenderUpdate(pos, pos);
                    return true;
                }
            }
        }
        return false;
    }

}
