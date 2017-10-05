package club.nsdn.nyasamarailway.TileEntities.Signals;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class TileEntityRailTriStateReceiver extends TileEntityRailPassiveReceiver {

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
