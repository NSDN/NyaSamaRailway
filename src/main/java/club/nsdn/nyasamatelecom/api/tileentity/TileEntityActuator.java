package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.thewdj.telecom.IPassive;
import org.thewdj.telecom.IRelay;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntityActuator extends TileEntityReceiver implements IRelay<TileEntity, TileEntityTransceiver>, IPassive {

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

    public void controlTarget(boolean state) {
        int meta = getTargetMetadata();
        if (meta < 0) return;
        setTargetMetadata((state && (meta & 0x8) == 0) ? meta | 0x8 : meta);
    }

    public int getTargetMetadata() {
        TileEntity target = getTarget();

        if (target == null) return -1;

        int meta = target.getWorldObj().getBlockMetadata(
                target.xCoord, target.yCoord, target.zCoord
        );

        return meta;
    }

    public boolean setTargetMetadata(int meta) {
        TileEntity target = getTarget();

        if (target == null) return false;

        target.getWorldObj().setBlockMetadataWithNotify(
                target.xCoord, target.yCoord, target.zCoord, meta, 3
        );
        target.getWorldObj().markBlockForUpdate(target.xCoord, target.yCoord, target.zCoord);
        return true;
    }

    public TileEntityActuator() {
        targetX = "null";
        targetY = "null";
        targetZ = "null";
    }

}
