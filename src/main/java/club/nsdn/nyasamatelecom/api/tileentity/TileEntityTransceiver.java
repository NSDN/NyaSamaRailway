package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.thewdj.telecom.ITransceiver;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TileEntityTransceiver extends TileEntityBase implements ITransceiver<TileEntityTransceiver> {

    public String transceiverX, transceiverY, transceiverZ;

    @Override
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
        tileEntity = getWorld().getTileEntity(new BlockPos(x, y, z));

        if (tileEntity == null) return null;
        if (!(tileEntity instanceof TileEntityTransceiver)) return null;

        return (TileEntityTransceiver) tileEntity;
    }

    @Override
    public void setTransceiver(TileEntityTransceiver transceiver) {
        if (transceiver == null) {
            transceiverX = "null";
            transceiverY = "null";
            transceiverZ = "null";
        } else {
            transceiverX = String.valueOf(transceiver.getPos().getX());
            transceiverY = String.valueOf(transceiver.getPos().getY());
            transceiverZ = String.valueOf(transceiver.getPos().getZ());
        }
    }

    @Override
    public boolean transceiverIsPowered() {
        TileEntityTransceiver transceiver = getTransceiver();
        if (transceiver == null) return false;
        return (transceiver.META & 8) != 0;
    }

    public TileEntityTransceiver() {
        transceiverX = "null";
        transceiverY = "null";
        transceiverZ = "null";
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);
        transceiverX = tagCompound.getString("transceiverX");
        transceiverY = tagCompound.getString("transceiverY");
        transceiverZ = tagCompound.getString("transceiverZ");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (getTransceiver() == null) {
            tagCompound.setString("transceiverX", "null");
            tagCompound.setString("transceiverY", "null");
            tagCompound.setString("transceiverZ", "null");
        } else {
            tagCompound.setString("transceiverX", transceiverX);
            tagCompound.setString("transceiverY", transceiverY);
            tagCompound.setString("transceiverZ", transceiverZ);
        }
        return super.toNBT(tagCompound);
    }

}
