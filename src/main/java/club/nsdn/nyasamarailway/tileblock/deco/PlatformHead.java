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
        public static final int USE_GEN = 2;

        public int use = USE_MOD;
        public String url = "null";
        public boolean wide = false;
        public boolean enabled = false;

        public boolean right = false;
        public boolean isDir = false;
        public int colorPri = 0x323232;
        public int colorSec = 0xbababa;
        public int colorAcc = 0xc00000;
        public String nowMain = "\u55b5\u7389\u6bbf";
        public String nowSub = "NyaSama";
        public String prevTop = "\u4e0a\u4e00\u7ad9 Prev Stop";
        public String prevMain = "NSDN\u5927\u53a6";
        public String prevSub = "NSDN Tower";
        public String nextTop = "\u4e0b\u4e00\u7ad9 Next Stop";
        public String nextMain = "MC\u5e7b\u60f3\u4e61";
        public String nextSub = "MC Gensokyo";
        public String prevMainS = "";
        public String prevSubS = "";
        public String nextMainS = "";
        public String nextSubS = "";

        public String dirHead = "R";
        public String dirLine = "R  \u53f7\u7ebf";
        public String dirLineSub = "Line R";
        public String dirTarget = "\u8679\u4e4b\u91cc\u65b9\u5411";
        public String dirTargetSub = "To Rainbow Village";

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

            right = tagCompound.getBoolean("right");
            isDir = tagCompound.getBoolean("isDir");
            colorPri = tagCompound.getInteger("colorPri");
            colorSec = tagCompound.getInteger("colorSec");
            colorAcc = tagCompound.getInteger("colorAcc");
            
            NBTTagCompound tag = tagCompound.getCompoundTag("now");
            if (tag.hasNoTags()) return;
            nowMain = tag.getString("nowMain");
            nowSub = tag.getString("nowSub");
            prevTop = tag.getString("prevTop");
            prevMain = tag.getString("prevMain");
            prevSub = tag.getString("prevSub");
            nextTop = tag.getString("nextTop");
            nextMain = tag.getString("nextMain");
            nextSub = tag.getString("nextSub");
            prevMainS = tag.getString("prevMainS");
            prevSubS = tag.getString("prevSubS");
            nextMainS = tag.getString("nextMainS");
            nextSubS = tag.getString("nextSubS");
            tag = tagCompound.getCompoundTag("dir");
            if (tag.hasNoTags()) return;
            dirHead = tag.getString("dirHead");
            dirLine = tag.getString("dirLine");
            dirLineSub = tag.getString("dirLineSub");
            dirTarget = tag.getString("dirTarget");
            dirTargetSub = tag.getString("dirTargetSub");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("use", use);
            tagCompound.setString("url", url);
            tagCompound.setBoolean("wide", wide);
            tagCompound.setBoolean("enabled", enabled);

            tagCompound.setBoolean("right", right);
            tagCompound.setBoolean("isDir", isDir);
            tagCompound.setInteger("colorPri", colorPri);
            tagCompound.setInteger("colorSec", colorSec);
            tagCompound.setInteger("colorAcc", colorAcc);

            NBTTagCompound now = new NBTTagCompound();
            now.setString("nowMain", nowMain);
            now.setString("nowSub", nowSub);
            now.setString("prevTop", prevTop);
            now.setString("prevMain", prevMain);
            now.setString("prevSub", prevSub);
            now.setString("nextTop", nextTop);
            now.setString("nextMain", nextMain);
            now.setString("nextSub", nextSub);
            now.setString("prevMainS", prevMainS);
            now.setString("prevSubS", prevSubS);
            now.setString("nextMainS", nextMainS);
            now.setString("nextSubS", nextSubS);
            tagCompound.setTag("now", now);
            NBTTagCompound dir = new NBTTagCompound();
            dir.setString("dirHead", dirHead);
            dir.setString("dirLine", dirLine);
            dir.setString("dirLineSub", dirLineSub);
            dir.setString("dirTarget", dirTarget);
            dir.setString("dirTargetSub", dirTargetSub);
            tagCompound.setTag("dir", dir);

            return super.toNBT(tagCompound);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox()
                    .expand(4, 4, 4)
                    .expand(-4, -4, -4);
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
                            else if (str.toLowerCase().equals("mod"))
                                head.use = TileEntityPlatformHead.USE_MOD;
                            else
                                head.use = TileEntityPlatformHead.USE_GEN;

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

                        funcList.put("right", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            head.right = ((int) dst.data) != 0;

                            return Result.OK;
                        }));
                        funcList.put("dir", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            head.isDir = ((int) dst.data) != 0;

                            return Result.OK;
                        }));

                        funcList.put("pri", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            head.colorPri = (int) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("sec", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            head.colorSec = (int) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("acc", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            head.colorAcc = (int) dst.data;

                            return Result.OK;
                        }));

                        funcList.put("now", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nowMain = (String) dst.data;
                            head.nowSub = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("now.main", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nowMain = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("now.sub", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nowSub = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("prev", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevMain = (String) dst.data;
                            head.prevSub = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("prev.main", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevMain = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("prev.sub", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevSub = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("next", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nextMain = (String) dst.data;
                            head.nextSub = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("next.main", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nextMain = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("next.sub", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nextSub = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("top", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevTop = (String) dst.data;
                            head.nextTop = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("top.prev", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevTop = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("top.next", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nextTop = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("pres", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevMainS = (String) dst.data;
                            head.prevSubS = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("pres.main", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevMainS = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("pres.sub", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.prevSubS = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("nexs", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nextMainS = (String) dst.data;
                            head.nextSubS = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("nexs.main", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nextMainS = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("nexs.sub", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.nextSubS = (String) dst.data;

                            return Result.OK;
                        }));

                        funcList.put("dir.head", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.dirHead = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("dir.line", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.dirLine = (String) dst.data;
                            head.dirLineSub = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("dir.line.main", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.dirLine = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("dir.line.sub", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.dirLineSub = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("dir.tar", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.dirTarget = (String) dst.data;
                            head.dirTargetSub = (String) src.data;

                            return Result.OK;
                        }));
                        funcList.put("dir.tar.main", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.dirTarget = (String) dst.data;

                            return Result.OK;
                        }));
                        funcList.put("dir.tar.sub", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            head.dirTargetSub = (String) dst.data;

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
