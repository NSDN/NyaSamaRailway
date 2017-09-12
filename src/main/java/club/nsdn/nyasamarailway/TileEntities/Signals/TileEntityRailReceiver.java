package club.nsdn.nyasamarailway.TileEntities.Signals;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.thewdj.telecom.IReceiver;

/**
 * Created by drzzm32 on 2017.6.17.
 */
public class TileEntityRailReceiver extends TileEntity implements IReceiver<TileEntityRailTransceiver> {

    public String receiverX, receiverY, receiverZ;

    public TileEntityRailTransceiver getSender() {
        if (receiverX.equals("null") || receiverY.equals("null") || receiverZ.equals("null")) return null;

        TileEntity tileEntity; int x, y, z;
        try {
            x = Integer.parseInt(receiverX);
            y = Integer.parseInt(receiverY);
            z = Integer.parseInt(receiverZ);
        } catch (Exception e) {
            return null;
        }
        tileEntity = worldObj.getTileEntity(x, y, z);

        if (tileEntity == null) return null;
        if (!(tileEntity instanceof TileEntityRailTransceiver)) return null;

        return (TileEntityRailTransceiver) tileEntity;
    }

    public void setSender(TileEntityRailTransceiver rail) {
        if (rail == null) {
            receiverX = "null";
            receiverY = "null";
            receiverZ = "null";
        } else {
            receiverX = String.valueOf(rail.xCoord);
            receiverY = String.valueOf(rail.yCoord);
            receiverZ = String.valueOf(rail.zCoord);
        }
    }

    public boolean senderIsPowered() {
        TileEntityRailTransceiver rail = getSender();
        if (rail == null) return false;
        int meta = worldObj.getBlockMetadata(rail.xCoord, rail.yCoord, rail.zCoord);
        return (meta & 8) != 0;
    }

    public TileEntityRailReceiver() {
        receiverX = "null";
        receiverY = "null";
        receiverZ = "null";
    }

    public void fromNBT(NBTTagCompound tagCompound) {
        receiverX = tagCompound.getString("receiverRailX");
        receiverY = tagCompound.getString("receiverRailY");
        receiverZ = tagCompound.getString("receiverRailZ");
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (getSender() == null) {
            tagCompound.setString("receiverRailX", "null");
            tagCompound.setString("receiverRailY", "null");
            tagCompound.setString("receiverRailZ", "null");
        } else {
            tagCompound.setString("receiverRailX", receiverX);
            tagCompound.setString("receiverRailY", receiverY);
            tagCompound.setString("receiverRailZ", receiverZ);
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

    public void updateTileEntity(TileEntity tileEntity) {
        if (tileEntity == null) return;
        tileEntity.getWorldObj().markBlockForUpdate(
                tileEntity.xCoord,
                tileEntity.yCoord,
                tileEntity.zCoord
        );
    }

    public void onDestroy() {
        if (getSender() != null) {
            if (getSender() instanceof TileEntityRailMultiSender) {
                ((TileEntityRailMultiSender) getSender()).decTarget();
                updateTileEntity(getSender());
            }
        }
    }

}
