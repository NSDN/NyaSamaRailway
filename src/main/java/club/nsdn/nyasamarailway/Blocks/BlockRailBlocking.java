package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.Items.Item74HC04;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by drzzm32 on 2017.6.16.
 */
public class BlockRailBlocking extends BlockRailDetectorBase implements ITileEntityProvider {

    public static LinkedHashMap<UUID, TileEntityRailBlocking> tmpRails;

    public static class TileEntityRailBlocking extends TileEntity {

        public TileEntityRailBlocking linkedRail;

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            if (linkedRail == null) return;
            tagCompound.setInteger("linkedRailX", linkedRail.xCoord);
            tagCompound.setInteger("linkedRailY", linkedRail.yCoord);
            tagCompound.setInteger("linkedRailZ", linkedRail.zCoord);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            int x, y, z;
            x = tagCompound.getInteger("linkedRailX");
            y = tagCompound.getInteger("linkedRailY");
            z = tagCompound.getInteger("linkedRailZ");
            if (!(worldObj.getTileEntity(x, y, z) instanceof TileEntityRailBlocking)) return;
            linkedRail = (TileEntityRailBlocking) worldObj.getTileEntity(x, y, z);
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            if (linkedRail == null) return null;
            tagCompound.setInteger("linkedRailX", linkedRail.xCoord);
            tagCompound.setInteger("linkedRailY", linkedRail.yCoord);
            tagCompound.setInteger("linkedRailZ", linkedRail.zCoord);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
            int x, y, z;
            x = tagCompound.getInteger("linkedRailX");
            y = tagCompound.getInteger("linkedRailY");
            z = tagCompound.getInteger("linkedRailZ");
            if (!(worldObj.getTileEntity(x, y, z) instanceof TileEntityRailBlocking)) return;
            linkedRail = (TileEntityRailBlocking) worldObj.getTileEntity(x, y, z);
        }
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailBlocking();
    }

    public BlockRailBlocking() {
        super("BlockRailBlocking");
        setTextureName("rail_blocking");

        tmpRails = new LinkedHashMap<UUID, TileEntityRailBlocking>();
    }

    public boolean checkNearbySameRail(World world, int x, int y, int z) {
        return world.getBlock(x + 1, y, z) == this || world.getBlock(x - 1, y, z) == this ||
                world.getBlock(x, y, z + 1) == this || world.getBlock(x, y, z - 1) == this;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {

    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityRailBlocking thisRail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailBlocking) {
            thisRail = (TileEntityRailBlocking) world.getTileEntity(x, y, z);
        }
        if (thisRail != null) {
            UUID uuid = player.getUniqueID();
            if (player.isSneaking() && !world.isRemote) {
                if (tmpRails.containsKey(uuid)) {
                    if (tmpRails.get(uuid) == thisRail) {
                        thisRail.linkedRail = null;
                        player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.abort"));
                    } else {
                        if (thisRail.linkedRail == tmpRails.get(uuid)) {
                            thisRail.linkedRail.linkedRail = null;
                            thisRail.linkedRail = null;
                            player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.disconnected"));
                        } else {
                            thisRail.linkedRail = tmpRails.get(uuid);
                            thisRail.linkedRail.linkedRail = thisRail;
                            player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.connected"));
                        }
                    }
                    tmpRails.remove(uuid);
                } else {
                    tmpRails.put(uuid, thisRail);
                    player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.begin"));
                }
                return true;
            }
        }
        return false;
    }

}
