package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntityTriStateReceiver extends TileEntityPassiveReceiver {

    public static final int STATE_POS = 1;
    public static final int STATE_ZERO = 0;
    public static final int STATE_NEG = -1;

    public int state;
    public int prevState;

    public void setStatePos() {
        state = state == STATE_NEG ? STATE_ZERO : STATE_POS;
    }

    public void setStateNeg() {
        state = state == STATE_POS ? STATE_ZERO : STATE_NEG;
    }

    public void fromNBT(NBTTagCompound tagCompound) {
        state = tagCompound.getInteger("state");
        prevState = tagCompound.getInteger("prevState");

    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("state", state);
        tagCompound.setInteger("prevState", prevState);
        return tagCompound;
    }

}
