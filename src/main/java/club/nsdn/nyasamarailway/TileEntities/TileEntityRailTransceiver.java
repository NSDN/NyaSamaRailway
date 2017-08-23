package club.nsdn.nyasamarailway.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2017.6.17.
 */
public class TileEntityRailTransceiver extends TileEntity {

    public String transceiverX, transceiverY, transceiverZ;

    public TileEntityRailTransceiver getTransceiverRail() {
        if (transceiverX.equals("null") || transceiverY.equals("null") || transceiverZ.equals("null")) return null;

        TileEntity tileEntity; int x, y, z;
        try {
            x = Integer.parseInt(transceiverX);
            y = Integer.parseInt(transceiverY);
            z = Integer.parseInt(transceiverZ);
        } catch (Exception e) {
            return null;
        }
        tileEntity = worldObj.getTileEntity(x, y, z);

        if (tileEntity == null) return null;
        if (!(tileEntity instanceof TileEntityRailTransceiver)) return null;

        return (TileEntityRailTransceiver) tileEntity;
    }

    public void setTransceiverRail(TileEntityRailTransceiver rail) {
        if (rail == null) {
            transceiverX = "null";
            transceiverY = "null";
            transceiverZ = "null";
        } else {
            transceiverX = String.valueOf(rail.xCoord);
            transceiverY = String.valueOf(rail.yCoord);
            transceiverZ = String.valueOf(rail.zCoord);
        }
    }

    public boolean transceiverRailIsPowered() {
        TileEntityRailTransceiver rail = getTransceiverRail();
        if (rail == null) return false;
        int meta = worldObj.getBlockMetadata(rail.xCoord, rail.yCoord, rail.zCoord);
        return (meta & 8) != 0;
    }

    public TileEntityRailTransceiver() {
        transceiverX = "null";
        transceiverY = "null";
        transceiverZ = "null";
    }

    public void fromNBT(NBTTagCompound tagCompound) {
        transceiverX = tagCompound.getString("transceiverRailX");
        transceiverY = tagCompound.getString("transceiverRailY");
        transceiverZ = tagCompound.getString("transceiverRailZ");
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (getTransceiverRail() == null) {
            tagCompound.setString("transceiverRailX", "null");
            tagCompound.setString("transceiverRailY", "null");
            tagCompound.setString("transceiverRailZ", "null");
        } else {
            tagCompound.setString("transceiverRailX", transceiverX);
            tagCompound.setString("transceiverRailY", transceiverY);
            tagCompound.setString("transceiverRailZ", transceiverZ);
        }
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
