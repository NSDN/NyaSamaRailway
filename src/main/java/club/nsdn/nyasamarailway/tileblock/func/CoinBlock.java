package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.misc.ItemNyaCoin;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class CoinBlock extends TileBlock {

    public static class TileEntityCoinBlock extends TileEntityBase {

        public int value = 1;

        public TileEntityCoinBlock() {
            setInfo(4, 1, 1, 1);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            value = tagCompound.getInteger("value");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("value", value);

            return super.toNBT(tagCompound);
        }

    }

    public CoinBlock() {
        super("CoinBlock");
        setRegistryName(NyaSamaRailway.MODID, "block_coin");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityCoinBlock();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            return dir != facing;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityCoinBlock) {
            TileEntityCoinBlock coinBlock = (TileEntityCoinBlock) tileEntity;
            if (!world.isRemote) {
                if (coinBlock.value <= 0) coinBlock.value = 1;
                int value = coinBlock.value;

                ItemStack stack = player.getHeldItemMainhand();
                if (stack.isEmpty()) {
                    ItemStack itemStack = new ItemStack(ItemLoader.nyaCoin);
                    ItemNyaCoin.setValue(itemStack, value);
                    itemStack.setStackDisplayName(itemStack.getDisplayName() + "(" + value + ")");
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemStack);
                    return true;
                }
            }
        }
        return false;
    }

}
