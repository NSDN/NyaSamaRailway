package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.tileentity.ITriStateReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public abstract class TileEntityTrackSideReception extends TileEntityRailReception implements ITriStateReceiver, ITrackSide {

    public ForgeDirection direction;

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
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );
        state = tagCompound.getInteger("state");
        prevState = tagCompound.getInteger("prevState");
        super.fromNBT(tagCompound);
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());
        tagCompound.setInteger("state", state);
        tagCompound.setInteger("prevState", prevState);
        return super.toNBT(tagCompound);
    }



}
