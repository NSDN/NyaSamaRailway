package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.thewdj.telecom.IReceiver;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntityReceiver extends TileEntity implements IReceiver<TileEntityTransceiver> {

    public String receiverX, receiverY, receiverZ;

    public TileEntityTransceiver getSender() {
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
        if (!(tileEntity instanceof TileEntityTransceiver)) return null;

        return (TileEntityTransceiver) tileEntity;
    }

    public void setSender(TileEntityTransceiver sender) {
        if (sender == null) {
            receiverX = "null";
            receiverY = "null";
            receiverZ = "null";
        } else {
            receiverX = String.valueOf(sender.xCoord);
            receiverY = String.valueOf(sender.yCoord);
            receiverZ = String.valueOf(sender.zCoord);
        }
    }

    public boolean senderIsPowered() {
        TileEntityTransceiver sender = getSender();
        if (sender == null) return false;
        int meta = worldObj.getBlockMetadata(sender.xCoord, sender.yCoord, sender.zCoord);
        return (meta & 8) != 0;
    }

    public TileEntityReceiver() {
        receiverX = "null";
        receiverY = "null";
        receiverZ = "null";
    }

    public void fromNBT(NBTTagCompound tagCompound) {
        receiverX = tagCompound.getString("receiverX");
        receiverY = tagCompound.getString("receiverY");
        receiverZ = tagCompound.getString("receiverZ");
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (getSender() == null) {
            tagCompound.setString("receiverX", "null");
            tagCompound.setString("receiverY", "null");
            tagCompound.setString("receiverZ", "null");
        } else {
            tagCompound.setString("receiverX", receiverX);
            tagCompound.setString("receiverY", receiverY);
            tagCompound.setString("receiverZ", receiverZ);
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
            if (getSender() instanceof TileEntityMultiSender) {
                ((TileEntityMultiSender) getSender()).decTarget();
                updateTileEntity(getSender());
            }
        }
    }

}
