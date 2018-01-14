package club.nsdn.nyasamarailway.tileblock.functional;

import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.ItemNyaCoin;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
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

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class BlockCoinBlock extends TileBlock {

    public static class CoinBlock extends TileEntity {

        public int value = 1;

        public void fromNBT(NBTTagCompound tagCompound) {
            value = tagCompound.getInteger("value");
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("value", value);
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

    public BlockCoinBlock() {
        super("CoinBlock");
        setIconLocation("block_coin");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new CoinBlock();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return true;
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int v, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof CoinBlock) {
                CoinBlock coinBlock = (CoinBlock) world.getTileEntity(x, y, z);
                if (coinBlock.value <= 0) coinBlock.value = 1;
                int value = coinBlock.value;

                ItemStack stack = player.getCurrentEquippedItem();
                if (stack == null) {
                    ItemStack itemStack = new ItemStack(ItemLoader.itemNyaCoin);
                    ItemNyaCoin.setValue(itemStack, value);
                    itemStack.setStackDisplayName(itemStack.getDisplayName() + "(" + value + ")");
                    player.setCurrentItemOrArmor(0, itemStack);
                    return true;
                }
            }
        }
        return false;
    }


}
