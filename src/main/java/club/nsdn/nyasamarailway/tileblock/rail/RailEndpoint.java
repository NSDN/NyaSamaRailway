package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.3.17.
 */
public class RailEndpoint extends BlockContainer {

    public static enum EnumType implements IStringSerializable {
        MONO("mono"),
        NS("ns"),
        SS("ss");

        private final String name;

        EnumType(String name) { this.name = name; }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public static EnumType from(int i) {
            int len = values().length;
            if (i >= len) i = 0;
            return values()[i];
        }
    }

    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);

    public static AxisAlignedBB AABB_PALM = FULL_BLOCK_AABB.contract(0.1875, 0, 0.1875).contract(-0.1875, 0, -0.1875);
    public static AxisAlignedBB AABB_PAD = FULL_BLOCK_AABB.contract(0.1875, 0, 0.1875).contract(-0.1875, -0.75, -0.1875);

    public static AxisAlignedBB AABB_RAIL = FULL_BLOCK_AABB.contract(0, 1 - 0.125, 0);

    public static final PropertyBool PALM = PropertyBool.create("palm");

    public static class TileEntityRailEndpoint extends club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint {

    }

    public static class TileEntityFastRailEndpoint extends club.nsdn.nyasamarailway.api.rail.TileEntityFastRailEndpoint {

    }

    public RailEndpoint() {
        super(Material.IRON);
        setUnlocalizedName("RailEndpoint");
        setRegistryName(NyaSamaRailway.MODID, "rail_endpoint");
        setLightOpacity(0);
        setHardness(2.0F);
        setResistance(blockHardness * 5.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumType.MONO).withProperty(PALM, false));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        EnumType type = EnumType.from(i);
        if (type == EnumType.MONO)
            return new TileEntityRailEndpoint();
        return new TileEntityFastRailEndpoint();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, PALM);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumType.from(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);
        if (state.getValue(TYPE) == EnumType.MONO)
            return state.withProperty(PALM, !world.getBlockState(pos.down()).getMaterial().isReplaceable());
        return state;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (state.getValue(TYPE) == EnumType.MONO)
            return super.getCollisionBoundingBox(state, world, pos);
        return Block.NULL_AABB;
    }

    @Override
    @Nullable
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (state.getValue(TYPE) == EnumType.MONO)
            return state.getValue(PALM) ? AABB_PALM : AABB_PAD;
        return AABB_RAIL;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    void say(EntityPlayer player, String format, Object... args) {
        player.sendMessage(new TextComponentString(String.format(format, args)));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() instanceof Item74HC04) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityRailEndpoint) {
                TileEntityRailEndpoint endpoint = (TileEntityRailEndpoint) te;

                if (!world.isRemote && player.isSneaking()){
                    endpoint.updateRoute();

                    Vec3d vec;
                    for (double i = 0; i <= endpoint.len(); i++) {
                        vec = endpoint.get(i);
                        if (Double.isNaN(vec.x + vec.y + vec.z) || Double.isInfinite(vec.x + vec.y + vec.z)) {
                            say(player, "[NSR] NAN ERROR | INF ERROR");
                            endpoint.clear();
                            return true;
                        }
                    }

                    say(player, "[NSR] Mono rail built.");
                    endpoint.refresh();
                }

                if (world.isRemote && !player.isSneaking()) {
                    endpoint.clearVertices();
                }

                return true;
            } else if (te instanceof TileEntityFastRailEndpoint) {
                TileEntityFastRailEndpoint endpoint = (TileEntityFastRailEndpoint) te;

                if (!world.isRemote && player.isSneaking()){
                    endpoint.updateRoute();

                    Vec3d vec;
                    for (double i = 0; i <= endpoint.len(); i++) {
                        vec = endpoint.get(i);
                        if (Double.isNaN(vec.x + vec.y + vec.z) || Double.isInfinite(vec.x + vec.y + vec.z)) {
                            say(player, "[NSR] NAN ERROR | INF ERROR");
                            endpoint.clear();
                            return true;
                        }
                    }

                    say(player, "[NSR] Normal rail built.");
                    endpoint.refresh();
                }

                if (world.isRemote && !player.isSneaking()) {
                    endpoint.clearModel();
                }

                return true;
            }
        } else if (stack.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) stack.getItem();
            Block block = itemBlock.getBlock();

            if (!world.isRemote) {
                state = world.getBlockState(pos);
                if (block instanceof RailNoSleeper) {
                    state = state.withProperty(TYPE, EnumType.NS);
                    world.setBlockState(pos, state);
                    return true;
                } else if (block instanceof RailStoneSleeper) {
                    state = state.withProperty(TYPE, EnumType.SS);
                    world.setBlockState(pos, state);
                    return true;
                } else if (block instanceof MonoRailBase) {
                    state = state.withProperty(TYPE, EnumType.MONO);
                    world.setBlockState(pos, state);
                    return true;
                }
            }
        }

        return false;
    }

}
