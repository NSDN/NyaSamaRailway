package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntityTriStateTransmitter extends TileEntityActuator {

    public boolean triStateIsNeg;
    public boolean prevTriStateIsNeg;
    public boolean inverterEnabled;
    public boolean prevInverterEnabled;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        triStateIsNeg = tagCompound.getBoolean("triStateIsNeg");
        inverterEnabled = tagCompound.getBoolean("inverterEnabled");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setBoolean("triStateIsNeg", triStateIsNeg);
        tagCompound.setBoolean("inverterEnabled", inverterEnabled);
        return super.toNBT(tagCompound);
    }

    public boolean setSwitch(boolean state) {
        if (getTarget() instanceof TileEntityTriStateReceiver) {
            TileEntityTriStateReceiver target = (TileEntityTriStateReceiver) getTarget();

            if (triStateIsNeg) {
                if (state) target.setStateNeg();
            } else {
                if (state) target.setStatePos();
            }

            return true;
        }
        return false;
    }

}
