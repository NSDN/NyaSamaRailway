package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.misc.ItemNyaCoin;
import club.nsdn.nyasamarailway.item.misc.ItemTicketBase;
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
 * Created by drzzm32 on 2019.2.10
 */
public class TicketBlockOnce extends TileBlock {

    public static class TileEntityTicketBlockOnce extends TileEntityBase {
        public static final int DELAY = 5;
        public int delay;

        public int setOver = 1;

        public TileEntityTicketBlockOnce() {
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

            setOver = tagCompound.getInteger("setOver");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("setOver", setOver);

            return super.toNBT(tagCompound);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityTicketBlockOnce) {
                TileEntityTicketBlockOnce ticketBlock = (TileEntityTicketBlockOnce) tileEntity;
                int meta = ticketBlock.META;

                if ((meta & 0x4) != 0) {
                    ticketBlock.delay += 1;
                    if (ticketBlock.delay > TileEntityTicketBlockOnce.DELAY * 20) {
                        ticketBlock.META = meta & 0x3;
                        ticketBlock.refresh();
                    }
                } else {
                    ticketBlock.delay = 0;
                }
            }
        }

    }

    public TicketBlockOnce() {
        super("TicketBlockOnce");
        setRegistryName(NyaSamaRailway.MODID, "block_ticket_once");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTicketBlockOnce();
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
        if (tileEntity instanceof TileEntityTicketBlockOnce) {
            TileEntityTicketBlockOnce ticketBlock = (TileEntityTicketBlockOnce) tileEntity;
            ItemStack stack = player.getHeldItemMainhand();
            int meta = ticketBlock.META;

            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemNyaCoin && ItemNyaCoin.getValue(stack) == ticketBlock.setOver) {
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    ticketBlock.META = meta | 0x4;
                    ticketBlock.refresh();

                    return true;
                }
            } else {
                if ((meta & 0x4) != 0) {
                    ItemStack itemStack = new ItemStack(ItemLoader.oneCard);
                    ItemTicketBase.setOver(itemStack, ticketBlock.setOver);
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemStack);
                    ticketBlock.META = meta & 0x3;
                    ticketBlock.refresh();

                    return true;
                }
            }
        }
        return false;
    }


}
