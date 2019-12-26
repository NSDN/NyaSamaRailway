package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.misc.ItemNyaCoin;
import club.nsdn.nyasamarailway.item.misc.ItemTicketBase;
import club.nsdn.nyasamarailway.item.misc.ItemTicketStore;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
public class TicketBlockCard extends TileBlock {

    public final int MAX_OVER = 500;

    public static class TileEntityTicketBlockCard extends TileEntityBase {

        public static final int DELAY = 5;
        public int delay;

        public int over;

        public TileEntityTicketBlockCard() {
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

            over = tagCompound.getInteger("over");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("over", over);

            return super.toNBT(tagCompound);
        }

        public EnumFacing dirFromMeta(int meta) {
            switch (meta & 0x3) {
                case 0: return EnumFacing.NORTH;
                case 1: return EnumFacing.EAST;
                case 2: return EnumFacing.SOUTH;
                case 3: return EnumFacing.WEST;
            }
            return EnumFacing.DOWN;
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityTicketBlockCard) {
                TileEntityTicketBlockCard ticketBlock = (TileEntityTicketBlockCard) tileEntity;
                int meta = ticketBlock.META;

                if ((meta & 0x4) != 0) {
                    ticketBlock.delay += 1;
                    if (ticketBlock.delay > TileEntityTicketBlockCard.DELAY * 20) {
                        ItemStack itemStack = new ItemStack(ItemLoader.nyaCoin);
                        ItemNyaCoin.setValue(itemStack, ticketBlock.over);
                        EnumFacing facing = dirFromMeta(meta);
                        EntityItem entityItem = new EntityItem(
                                world,
                                pos.getX() + 0.5 + facing.getFrontOffsetX(),
                                pos.getY() + 0.5 + facing.getFrontOffsetY(),
                                pos.getZ() + 0.5 + facing.getFrontOffsetZ(),
                                itemStack
                        );
                        world.spawnEntity(entityItem);
                        ticketBlock.over = 0;
                        ticketBlock.META = meta & 0x3;
                        ticketBlock.refresh();
                    }
                } else if ((meta & 0x8) != 0) {
                    ticketBlock.delay += 1;
                    if (ticketBlock.delay > TileEntityTicketBlockCard.DELAY * 10) {
                        ticketBlock.META = meta & 0x3;
                        ticketBlock.refresh();
                    }
                } else {
                    ticketBlock.delay = 0;
                }
            }
        }

    }

    public TicketBlockCard() {
        super("TicketBlockCard");
        setRegistryName(NyaSamaRailway.MODID, "block_ticket_card");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTicketBlockCard();
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
        if (tileEntity instanceof TileEntityTicketBlockCard) {
            TileEntityTicketBlockCard ticketBlock = (TileEntityTicketBlockCard) tileEntity;
            ItemStack stack = player.getHeldItemMainhand();
            int meta = ticketBlock.META;

            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemNyaCoin) {
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    ticketBlock.over = ItemNyaCoin.getValue(stack);
                    ticketBlock.META = meta | 0x4;
                    ticketBlock.refresh();

                    return true;
                } else if (stack.getItem() instanceof ItemTicketBase) {
                    if ((meta & 0x4) != 0 && stack.getItem() instanceof ItemTicketStore) {
                        int over = ItemTicketBase.getOver(stack);
                        if (over > MAX_OVER) {
                            ItemStack itemStack = new ItemStack(ItemLoader.nyaCoin);
                            ItemNyaCoin.setValue(itemStack, ticketBlock.over);
                            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemStack);
                            ticketBlock.over = 0;
                        } else {
                            ItemTicketBase.setOver(stack, over + ticketBlock.over);
                        }
                    }
                    ItemTicketBase.resetCard(stack);
                    ticketBlock.over = ItemTicketBase.getOver(stack);
                    ticketBlock.META = (meta & 0x3) | 0x8;
                    ticketBlock.refresh();

                    return true;
                }
            } else {
                if ((meta & 0x4) != 0 && ticketBlock.over >= 20) {
                    ItemStack itemStack = new ItemStack(ItemLoader.nyaCard);
                    ItemTicketBase.setOver(itemStack, ticketBlock.over);
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemStack);
                    ticketBlock.META = meta & 0x3;
                    ticketBlock.over = 0;

                    return true;
                }
            }

        }
        return false;
    }

}
