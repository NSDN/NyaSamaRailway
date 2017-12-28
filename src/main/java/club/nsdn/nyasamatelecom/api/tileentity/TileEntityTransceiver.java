package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.thewdj.telecom.ITransceiver;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntityTransceiver extends TileEntity implements ITransceiver<TileEntityTransceiver> {

    public String transceiverX, transceiverY, transceiverZ;

    public TileEntityTransceiver getTransceiver() {
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
        if (!(tileEntity instanceof TileEntityTransceiver)) return null;

        return (TileEntityTransceiver) tileEntity;
    }

    public void setTransceiver(TileEntityTransceiver rail) {
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

    public boolean transceiverIsPowered() {
        TileEntityTransceiver rail = getTransceiver();
        if (rail == null) return false;
        int meta = worldObj.getBlockMetadata(rail.xCoord, rail.yCoord, rail.zCoord);
        return (meta & 8) != 0;
    }

    public TileEntityTransceiver() {
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
        if (getTransceiver() == null) {
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
