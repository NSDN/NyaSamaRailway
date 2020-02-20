package club.nsdn.nyasamarailway.tileblock.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2020.2.20.
 */
public class PlatformHeadCorner extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

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

    public final double x = 0.625, y = 1, z = 0.625;
    public final boolean isHalf;

    public PlatformHeadCorner(String name, String id, boolean isHalf) {
        super(Material.IRON);
        this.isHalf = isHalf;
        setUnlocalizedName(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setLightOpacity(0);
        setHardness(2.0F);
        setResistance(blockHardness * 5.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
        AxisAlignedBB aabb = getBoxByXYZ(state, x, y, z);
        if (isHalf)
            aabb = aabb.contract(0, -0.5, 0);
        return aabb;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
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
        return state.withProperty(FACING, player.getHorizontalFacing().getOpposite());
    }

}
