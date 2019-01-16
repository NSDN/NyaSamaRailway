package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.thewdj.telecom.IReceiver;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TileEntityReceiver extends TileEntityBase implements IReceiver<TileEntityTransceiver> {

    public String receiverX, receiverY, receiverZ;

    @Override
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
        tileEntity = getWorld().getTileEntity(new BlockPos(x, y, z));

        if (tileEntity == null) return null;
        if (!(tileEntity instanceof TileEntityTransceiver)) return null;

        return (TileEntityTransceiver) tileEntity;
    }

    @Override
    public void setSender(TileEntityTransceiver sender) {
        if (sender == null) {
            receiverX = "null";
            receiverY = "null";
            receiverZ = "null";
        } else {
            receiverX = String.valueOf(sender.getPos().getX());
            receiverY = String.valueOf(sender.getPos().getY());
            receiverZ = String.valueOf(sender.getPos().getZ());
        }
    }

    @Override
    public boolean senderIsPowered() {
        TileEntityTransceiver sender = getSender();
        if (sender == null) return false;
        return (sender.META & 8) != 0;
    }

    public TileEntityReceiver() {
        receiverX = "null";
        receiverY = "null";
        receiverZ = "null";
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);
        receiverX = tagCompound.getString("receiverX");
        receiverY = tagCompound.getString("receiverY");
        receiverZ = tagCompound.getString("receiverZ");
    }

    @Override
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
        return super.toNBT(tagCompound);
    }

    @Override
    public void onDestroy() {
        if (getSender() != null) {
            if (getSender() instanceof TileEntityMultiSender) {
                ((TileEntityMultiSender) getSender()).decTarget();
                getSender().update();
            }
        }
    }

}
