package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

/**
 * Created by drzzm32 on 2017.9.12.
 */
public class TileEntityGlassShieldBase extends TileEntityRailReceiver {

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

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB
                .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                .expand(4, 4, 4);
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        progress = tagCompound.getInteger("progress");
        state = tagCompound.getInteger("state");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("progress", progress);
        tagCompound.setInteger("state", state);
        return super.toNBT(tagCompound);
    }

}
