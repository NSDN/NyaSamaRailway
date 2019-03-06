package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.3.6.
 */
public class BlockGlassShieldCorner extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool FULL = PropertyBool.create("full");

    static LinkedList<AxisAlignedBB> AABBs = new LinkedList<>();
    static AxisAlignedBB getAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        AxisAlignedBB aabb = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
        for (AxisAlignedBB i : AABBs) {
            if (i.equals(aabb)) return i;
        }
        AABBs.add(aabb);
        return aabb;
    }

    protected AxisAlignedBB getBoxByXYZ(IBlockState state, double x, double y, double z) {
        double x1 = 0, y1 = 0, z1 = 0;
        double x2 = x, y2 = y, z2 = z;
        EnumFacing facing = state.getValue(FACING);

        switch (facing) {
            case NORTH:
                return getAABB(x1, y1, z1, x2, y2, z2);
            case EAST:
                return getAABB(1.0 - z2, y1, x1, 1.0 - z1, y2, x2);
            case SOUTH:
                return getAABB(1.0 - x2, y1, 1.0 - z2, 1.0 - x1, y2, 1.0 - z1);
            case WEST:
                return getAABB(z1, y1, 1.0 - x2, z2, y2, 1.0 - x1);
        }

        return Block.FULL_BLOCK_AABB;
    }

    public final double x = 0.5625, y = 0.5, z = 0.5625;
    public final boolean isFull;

    public BlockGlassShieldCorner(String name, String id, boolean isFull) {
        super(Material.IRON);
        this.isFull = isFull;

        setUnlocalizedName(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setHardness(2.0F);
        setResistance(blockHardness * 5.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);

        setDefaultState(blockState.getBaseState()
                        .withProperty(FACING, EnumFacing.NORTH)
                        .withProperty(FULL, isFull)
        );
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getBoxByXYZ(state, x, state.getValue(FULL) ? y * 2 : y, z);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(FULL, (meta & 0x4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(FULL) ? 0x4 : 0x0);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, FULL);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        EnumFacing dir = state.getValue(FACING);
        if (facing.getAxis() == EnumFacing.Axis.Y)
            return facing == EnumFacing.DOWN || facing == EnumFacing.UP;
        return dir == facing.rotateY() || dir == facing;
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
        return state.withProperty(FACING, player.getHorizontalFacing().getOpposite()).withProperty(FULL, isFull);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = state.getBlock();
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() && !state.getValue(FULL)) {
                if (stack.getItem() == ItemBlock.getItemFromBlock(block)) {
                    world.setBlockState(pos, state.withProperty(FULL, true));
                    return true;
                }
            }
        }
        return false;
    }

}
