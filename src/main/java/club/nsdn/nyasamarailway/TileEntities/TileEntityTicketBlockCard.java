package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Items.ItemNyaCoin;
import club.nsdn.nyasamarailway.Items.ItemTicketBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class TileEntityTicketBlockCard extends TileEntityBase {

    public static class TicketBlock extends TileEntity {

        public static final int DELAY = 5;
        public int delay;

        public int over;

        public void fromNBT(NBTTagCompound tagCompound) {
            over = tagCompound.getInteger("over");
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("over", over);
            return tagCompound;
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            toNBT(tagCompound);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            fromNBT(tagCompound);
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            toNBT(tagCompound);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
            fromNBT(tagCompound);
        }

    }

    public TileEntityTicketBlockCard() {
        super("TicketBlockCard");
        setIconLocation("block_ticket_card");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TicketBlock();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof TicketBlock) {
                TicketBlock ticketBlock = (TicketBlock) world.getTileEntity(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);

                if ((meta & 0x4) != 0) {
                    ticketBlock.delay += 1;
                    if (ticketBlock.delay > TicketBlock.DELAY * 20) {
                        world.setBlockMetadataWithNotify(x, y, z, meta & 0x3, 3);
                        world.markBlockForUpdate(x, y, z);
                    }
                } else if ((meta & 0x8) != 0) {
                    ticketBlock.delay += 1;
                    if (ticketBlock.delay > TicketBlock.DELAY * 10) {
                        world.setBlockMetadataWithNotify(x, y, z, meta & 0x3, 3);
                        world.markBlockForUpdate(x, y, z);
                    }
                } else {
                    ticketBlock.delay = 0;
                }

                world.scheduleBlockUpdate(x, y, z, this, 1);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int v, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ItemStack stack = player.getCurrentEquippedItem();
            int meta = world.getBlockMetadata(x, y, z);

            if (world.getTileEntity(x, y, z) instanceof TicketBlock) {
                TicketBlock ticketBlock = (TicketBlock) world.getTileEntity(x, y, z);

                if (stack != null) {
                    if (stack.getItem() instanceof ItemNyaCoin) {
                        player.destroyCurrentEquippedItem();
                        ticketBlock.over = ItemNyaCoin.getValue(stack);
                        world.setBlockMetadataWithNotify(x, y, z, meta | 0x4, 3);
                        world.markBlockForUpdate(x, y, z);

                        return true;
                    } else if (stack.getItem() instanceof ItemTicketBase) {
                        if ((meta & 0x4) != 0) {
                            int over = ItemTicketBase.getOver(stack);
                            ItemTicketBase.setOver(stack, over + ticketBlock.over);
                        }
                        ticketBlock.over = ItemTicketBase.getOver(stack);
                        world.setBlockMetadataWithNotify(x, y, z, meta | 0x8, 3);
                        world.markBlockForUpdate(x, y, z);

                        return true;
                    }
                } else {
                    if ((meta & 0x4) != 0 && ticketBlock.over >= 20) {
                        ItemStack itemStack = new ItemStack(ItemLoader.itemNyaCard);
                        ItemTicketBase.setOver(itemStack, ticketBlock.over);
                        player.setCurrentItemOrArmor(0, itemStack);
                        ticketBlock.over = 0;

                        return true;
                    }
                }
            }

        }
        return false;
    }


}