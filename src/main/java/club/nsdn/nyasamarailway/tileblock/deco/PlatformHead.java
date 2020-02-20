package club.nsdn.nyasamarailway.tileblock.deco;

import club.nsdn.nyasamaoptics.util.font.FontLoader;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.func.TileEntityBuildEndpoint;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.item.tool.ItemNGTablet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2020.2.20.
 */
public class PlatformHead extends BlockContainer {

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
        double x1 = 0.5 - x / 2, y1 = 0, z1 = 0.5 - z / 2;
        double x2 = 0.5 + x / 2, y2 = y, z2 = 0.5 + z / 2;
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

    public final double x = 1, y = 1, z = 0.25;
    public final boolean isHalf;

    public static class TileEntityPlatformHead extends TileEntityBase {

        public static final int USE_MOD = 0;
        public static final int USE_WEB = 1;

        public int use = 0;
        public String url = "null";
        public boolean wide = false;
        public boolean enabled = false;

        @SideOnly(Side.CLIENT)
        public Object texture = null;

        public TileEntityPlatformHead() {
            super();
            setInfo(4, 1, 1, 0.25);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            use = tagCompound.getInteger("use");
            url = tagCompound.getString("url");
            wide = tagCompound.getBoolean("wide");
            enabled = tagCompound.getBoolean("enabled");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("use", use);
            tagCompound.setString("url", url);
            tagCompound.setBoolean("wide", wide);
            tagCompound.setBoolean("enabled", enabled);

            return super.toNBT(tagCompound);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

    }

    public PlatformHead(String name, String id, boolean isHalf) {
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityPlatformHead();
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
        if (facing.getAxis() == EnumFacing.Axis.Y) return true;
        return dir == facing.rotateY() || dir == facing.rotateY().getOpposite();
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

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemNGTablet) {
            NBTTagList list = Util.getTagListFromNGT(stack);
            if (list == null) return false;

            TileEntity tileEntity = world.getTileEntity(pos);
            if (!(tileEntity instanceof TileEntityPlatformHead))
                return false;

            if (!world.isRemote) {
                TileEntityPlatformHead head = (TileEntityPlatformHead) tileEntity;

                String[][] code = NSASM.getCode(list);
                new NSASM(code) {
                    @Override
                    public World getWorld() {
                        return world;
                    }

                    @Override
                    public double getX() {
                        return pos.getX();
                    }

                    @Override
                    public double getY() {
                        return pos.getY();
                    }

                    @Override
                    public double getZ() {
                        return pos.getZ();
                    }

                    @Override
                    public EntityPlayer getPlayer() {
                        return player;
                    }

                    @Override
                    public SimpleNetworkWrapper getWrapper() {
                        return NetworkWrapper.instance;
                    }

                    @Override
                    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                        funcList.put("use", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            String str = (String) dst.data;
                            if (str.toLowerCase().equals("web"))
                                head.use = TileEntityPlatformHead.USE_WEB;
                            else
                                head.use = TileEntityPlatformHead.USE_MOD;

                            return Result.OK;
                        }));

                        funcList.put("url", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.url = (String) dst.data;

                            return Result.OK;
                        }));

                        funcList.put("enb", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            head.enabled = ((int) dst.data) != 0;
                            head.refresh();

                            return Result.OK;
                        }));

                        funcList.put("wide", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            head.wide = ((int) dst.data) != 0;
                            head.refresh();

                            return Result.OK;
                        }));
                    }
                }.run();

            }

            return true;
        }

        return false;
    }

}
