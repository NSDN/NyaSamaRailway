package club.nsdn.nyasamarailway.tileblock.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.9.6.
 */
public class GlassShieldAlHalf extends TileBlock {

    public static class TileEntityGlassShieldAlHalf extends TileEntityBase {

        public TileEntityGlassShieldAlHalf() {
            setInfo(4, 1, 0.5, 0.125);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

    }

    public final GlassShieldAl fullBlock;

    public GlassShieldAlHalf(GlassShieldAl fullBlock) {
        super("GlassShieldAlHalf");
        this.fullBlock = fullBlock;

        setRegistryName(NyaSamaRailway.MODID, "glass_shield_al_half");
        setLightOpacity(1);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGlassShieldAlHalf();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            if (facing.getAxis() == EnumFacing.Axis.Y)
                return facing == EnumFacing.DOWN;
            return dir == facing.rotateY() || dir == facing.rotateY().getOpposite();
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                if (stack.getItem() == ItemBlock.getItemFromBlock(block)) {
                    int meta = getDeviceMeta(world, pos);
                    world.setBlockState(pos, fullBlock.getDefaultState());
                    setDeviceMeta(world, pos, meta);
                    return true;
                }
            }
        }
        return false;
    }

}
