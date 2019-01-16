package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.thewdj.telecom.IPassive;
import org.thewdj.telecom.IRelay;

/**
 * Created by drzzm32 on 2018.12.13.
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

    @Override
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
        tileEntity = getWorld().getTileEntity(new BlockPos(x, y, z));

        if (tileEntity == null) return null;

        return tileEntity;
    }

    @Override
    public void setTarget(TileEntity target) {
        if (target == null) {
            targetX = "null";
            targetY = "null";
            targetZ = "null";
        } else {
            targetX = String.valueOf(target.getPos().getX());
            targetY = String.valueOf(target.getPos().getY());
            targetZ = String.valueOf(target.getPos().getZ());
        }
    }

    @Override
    public void controlTarget(boolean state) {
        int meta = getTargetMetadata();
        if (meta < 0) return;
        setTargetMetadata((state && (meta & 0x8) == 0) ? meta | 0x8 : meta);
    }

    public int getTargetMetadata() {
        TileEntity target = getTarget();

        if (target == null) return -1;

        if (target instanceof TileEntityBase)
            return ((TileEntityBase) target).META;

        return -1;
    }

    public boolean setTargetMetadata(int meta) {
        TileEntity target = getTarget();

        if (target == null) return false;

        if (target instanceof TileEntityBase) {
            ((TileEntityBase) target).META = meta;
            ((TileEntityBase) target).refresh();
            return true;
        }

        return false;
    }

    public TileEntityActuator() {
        targetX = "null";
        targetY = "null";
        targetZ = "null";
    }

}
