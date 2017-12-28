package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntitySingleSender extends TileEntityTransceiver {

    public String targetX, targetY, targetZ;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        targetX = tagCompound.getString("targetX");
        targetY = tagCompound.getString("targetY");
        targetZ = tagCompound.getString("targetZ");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (getTarget() == null) {
            tagCompound.setString("targetX", "null");
            tagCompound.setString("targetY", "null");
            tagCompound.setString("targetZ", "null");
        } else {
            tagCompound.setString("targetX", targetX);
            tagCompound.setString("targetY", targetY);
            tagCompound.setString("targetZ", targetZ);
        }
        return super.toNBT(tagCompound);
    }

    public TileEntity getTarget() {
        if (targetX.equals("null") || targetY.equals("null") || targetZ.equals("null")) return null;

        TileEntity tileEntity; int x, y, z;
        try {
            x = Integer.parseInt(targetX);
            y = Integer.parseInt(targetY);
            z = Integer.parseInt(targetZ);
        } catch (Exception e) {
            return null;
        }
        tileEntity = worldObj.getTileEntity(x, y, z);

        if (tileEntity == null) return null;

        return tileEntity;
    }

    public void setTarget(TileEntity target) {
        if (target == null) {
            targetX = "null";
            targetY = "null";
            targetZ = "null";
        } else {
            targetX = String.valueOf(target.xCoord);
            targetY = String.valueOf(target.yCoord);
            targetZ = String.valueOf(target.zCoord);
        }
    }

    public boolean setMetadata(int meta) {
        this.getWorldObj().setBlockMetadataWithNotify(
                this.xCoord, this.yCoord, this.zCoord, meta, 3
        );
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    public TileEntitySingleSender() {
        targetX = "null";
        targetY = "null";
        targetZ = "null";
    }

}
