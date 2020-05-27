package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.rail.IVirtualRail;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.api.device.SignalBox;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2020.5.27.
 */
public class WireRailV extends SignalBox implements IVirtualRail {

    public static class TileEntityWireRailV extends TileEntitySignalBox {

        @SideOnly(Side.CLIENT)
        public LinkedList<BakedQuad> srcQuads;
        @SideOnly(Side.CLIENT)
        public LinkedList<BakedQuad> dstQuads;

        public TileEntityWireRailV() {

        }

        @Override
        public boolean hasFastRenderer() {
            return true;
        }

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
        }

        @Override
        @SideOnly(Side.CLIENT)
        @Nonnull
        public AxisAlignedBB getRenderBoundingBox()
        {
            return INFINITE_EXTENT_AABB;
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 65536.0;
        }

    }

    @Override
    public Vec3d getTargetDirection(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityWireRailV) {
            TileEntityWireRailV rail = (TileEntityWireRailV) tileEntity;
            if (rail.getTarget() != null) {
                TileEntity target = rail.getTarget();
                Vec3d dst = new Vec3d(target.getPos());
                Vec3d src = new Vec3d(rail.getPos());
                return dst.subtract(src).normalize();
            }
        }
        return Vec3d.ZERO;
    }

    public final AxisAlignedBB AABB;

    public WireRailV() {
        super(NyaSamaRailway.MODID, "WireRailV", "rail_wire_v");
        setHardness(2.0F);
        setResistance(blockHardness * 5.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);

        final double x = 0.25, y = 1, z = 0.25;
        this.AABB = new AxisAlignedBB(
                0.5 - x / 2, 0.5 - y / 2, 0.5 - z / 2,
                0.5 + x / 2, 0.5 + y / 2, 0.5 + z / 2
        );
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return createNewTileEntity();
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityWireRailV();
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
        return AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return false;
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
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return true;
    }

}
