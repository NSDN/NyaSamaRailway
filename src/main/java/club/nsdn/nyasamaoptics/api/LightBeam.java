package club.nsdn.nyasamaoptics.api;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class LightBeam extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public Class<? extends Block> source;
    public int lightType;

    public static final int TYPE_DOT = 0;
    public static final int TYPE_LINE = 1;

    public LightBeam(Class<? extends Block> source, int lightType) {
        super(Material.VINE);
        this.source = source;
        this.lightType = lightType;
        setLightLevel(1.0F);
        setLightOpacity(0);
        setHardness(-1.0F);
        setResistance(0xFFFFFF);
        setUnlocalizedName("lightBeam" + lightType);
        setRegistryName(NyaSamaOptics.MODID, "light_beam_" + lightType);
    }

    public LightBeam(Class<? extends Block> source, int lightType, float lightLevel) {
        super(Material.VINE);
        this.source = source;
        this.lightType = lightType;
        setLightLevel(lightLevel);
        setLightOpacity(0);
        setHardness(-1.0F);
        setResistance(0xFFFFFF);
        setUnlocalizedName("lightBeam" + lightType);
        setRegistryName(NyaSamaOptics.MODID, "light_beam_" + lightType);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return NULL_AABB;
    }
    @Override
    public boolean canCollideCheck(IBlockState state, boolean v) {
        return false;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
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
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean canBeReplacedByLeaves(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, float f, int i) {
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        if (world instanceof World)
            checkNearby((World) world, pos);
    }

    public void lightCtl(World world, BlockPos pos, boolean state) {
        lightCtl(world, pos, this, state);
    }

    public void lightCtl(World world, BlockPos pos, EnumFacing dir, int length, boolean state) {
        lightCtl(world, pos, this, dir, length, state);
    }

    /******************************************************************************************************************/

    public static EnumFacing getDir(World world, BlockPos pos) {
        return world.getBlockState(pos).getValue(FACING);
    }

    public static int getMetaFromDir(EnumFacing dir) {
        return dir.getIndex();
    }

    public static boolean isSource(World world, BlockPos pos, EnumFacing offset) {
        if (world.getBlockState(pos).getBlock() instanceof LightBeam) {
            LightBeam lightBeam = (LightBeam) world.getBlockState(pos).getBlock();
            return lightBeam.source.isInstance(world.getBlockState(pos.offset(offset)).getBlock());
        }
        return false;
    }

    public static boolean isMe(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof LightBeam;
    }

    public static boolean isMe(World world, BlockPos pos, EnumFacing offset) {
        return world.getBlockState(pos.offset(offset)).getBlock() instanceof LightBeam;
    }

    public static boolean placeLight(World world, BlockPos pos, LightBeam lightBeam, int meta) {
        if (meta == -1) {
            if (isMe(world, pos)) {
                world.setBlockToAir(pos);
            } else return false;
        } else {
            if (world.isAirBlock(pos) && !isMe(world, pos)) {
                world.setBlockState(pos, lightBeam.getStateFromMeta(meta), 3);
            } else return false;
        }

        return true;
    }

    public static void lightCtl(World world, BlockPos pos, LightBeam lightBeam, boolean state) {
        int meta = state ? 0 : -1;

        placeLight(world, pos.east(), lightBeam, meta);
        placeLight(world, pos.west(), lightBeam, meta);
        placeLight(world, pos.up(), lightBeam, meta);
        placeLight(world, pos.down(), lightBeam, meta);
        placeLight(world, pos.south(), lightBeam, meta);
        placeLight(world, pos.north(), lightBeam, meta);
    }

    public static void lightCtl(World world, BlockPos pos, LightBeam lightBeam, EnumFacing dir, int length, boolean state) {
        int meta = state ? getMetaFromDir(dir) : -1;

        for (int i = 1; i < length; i++) {
            if (!placeLight(world, pos.offset(dir, i), lightBeam, meta)) break;
        }
    }

    public static void checkNearby(World world, BlockPos pos) {
        boolean keepAlive = false;

        if (world.getBlockState(pos).getBlock() instanceof LightBeam) {
            LightBeam lightBeam = (LightBeam) world.getBlockState(pos).getBlock();

            switch (lightBeam.lightType) {
                case TYPE_DOT:
                    keepAlive |= isSource(world, pos, EnumFacing.UP);
                    keepAlive |= isSource(world, pos, EnumFacing.DOWN);
                    keepAlive |= isSource(world, pos, EnumFacing.NORTH);
                    keepAlive |= isSource(world, pos, EnumFacing.SOUTH);
                    keepAlive |= isSource(world, pos, EnumFacing.WEST);
                    keepAlive |= isSource(world, pos, EnumFacing.EAST);
                    break;
                case TYPE_LINE:
                    EnumFacing dir = getDir(world, pos).getOpposite();
                    keepAlive = isSource(world, pos, dir) || isMe(world, pos, dir);
                    break;
            }
        }

        if (!keepAlive) world.setBlockToAir(pos);
    }

}
