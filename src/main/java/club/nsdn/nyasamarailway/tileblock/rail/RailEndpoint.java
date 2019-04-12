package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

    public static AxisAlignedBB AABB_PALM = FULL_BLOCK_AABB.contract(0.1875, 0, 0.1875).contract(-0.1875, 0, -0.1875);
    public static AxisAlignedBB AABB_PAD = FULL_BLOCK_AABB.contract(0.1875, 0, 0.1875).contract(-0.1875, -0.75, -0.1875);

    public static final PropertyBool PALM = PropertyBool.create("palm");

    public static class TileEntityRailEndpoint extends club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint {

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
        setDefaultState(blockState.getBaseState().withProperty(PALM, true));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailEndpoint();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PALM);
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
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);
        return state.withProperty(PALM, !world.getBlockState(pos.down()).getMaterial().isReplaceable());
    }

    @Override
    @Nullable
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        return state.getValue(PALM) ? AABB_PALM : AABB_PAD;
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

                    say(player, "[NSR] Rail built.");
                    endpoint.refresh();
                }

                if (world.isRemote && !player.isSneaking()) {
                    endpoint.clearModel();
                }

                return true;
            }
        }

        return false;
    }

}
