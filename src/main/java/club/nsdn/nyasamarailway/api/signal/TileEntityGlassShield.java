package club.nsdn.nyasamarailway.api.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.9.12.
 */
public class TileEntityGlassShield extends TileEntityReceiver {

    public int progress = 0;
    public float prevDist;

    public static final int DELAY = 2;
    public int delay;

    public static final int PROGRESS_MAX = 32;

    public static final int STATE_CLOSE = 0;
    public static final int STATE_CLOSING = 1;
    public static final int STATE_OPEN = 2;
    public static final int STATE_OPENING = 3;
    public int state = STATE_CLOSE;

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(4, 4, 4);
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);

        progress = tagCompound.getInteger("progress");
        state = tagCompound.getInteger("state");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("progress", progress);
        tagCompound.setInteger("state", state);

        return super.toNBT(tagCompound);
    }

}
