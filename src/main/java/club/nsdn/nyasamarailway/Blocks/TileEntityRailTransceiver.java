package club.nsdn.nyasamarailway.Blocks;

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

        TileEntity tileEntity;
        try {
            tileEntity = worldObj.getTileEntity(
                    Integer.parseInt(transceiverX), Integer.parseInt(transceiverY), Integer.parseInt(transceiverZ)
            );
        } catch (Exception e) {
            return null;
        }
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

    public TileEntityRailTransceiver() {
        transceiverX = "null";
        transceiverY = "null";
        transceiverZ = "null";
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        if (getTransceiverRail() == null) {
            tagCompound.setString("transceiverRailX", "null");
            tagCompound.setString("transceiverRailY", "null");
            tagCompound.setString("transceiverRailZ", "null");
        }
        tagCompound.setString("transceiverRailX", transceiverX);
        tagCompound.setString("transceiverRailY", transceiverY);
        tagCompound.setString("transceiverRailZ", transceiverZ);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        transceiverX = tagCompound.getString("transceiverRailX");
        transceiverY = tagCompound.getString("transceiverRailY");
        transceiverZ = tagCompound.getString("transceiverRailZ");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        if (getTransceiverRail() == null) {
            tagCompound.setString("transceiverRailX", "null");
            tagCompound.setString("transceiverRailY", "null");
            tagCompound.setString("transceiverRailZ", "null");
        }
        tagCompound.setString("transceiverRailX", transceiverX);
        tagCompound.setString("transceiverRailY", transceiverY);
        tagCompound.setString("transceiverRailZ", transceiverZ);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        NBTTagCompound tagCompound = packet.func_148857_g();
        transceiverX = tagCompound.getString("transceiverRailX");
        transceiverY = tagCompound.getString("transceiverRailY");
        transceiverZ = tagCompound.getString("transceiverRailZ");
    }
}
