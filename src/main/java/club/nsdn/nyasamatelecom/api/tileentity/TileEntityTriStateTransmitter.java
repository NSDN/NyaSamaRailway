package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TileEntityTriStateTransmitter extends TileEntityActuator {

    public boolean triStateIsNeg;
    public boolean prevTriStateIsNeg;
    public boolean inverterEnabled;
    public boolean prevInverterEnabled;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);
        triStateIsNeg = tagCompound.getBoolean("triStateIsNeg");
        inverterEnabled = tagCompound.getBoolean("inverterEnabled");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setBoolean("triStateIsNeg", triStateIsNeg);
        tagCompound.setBoolean("inverterEnabled", inverterEnabled);
        return super.toNBT(tagCompound);
    }

    public boolean setSwitch(boolean state) {
        if (getTarget() instanceof ITriStateReceiver) {
            ITriStateReceiver target = (ITriStateReceiver) getTarget();

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
