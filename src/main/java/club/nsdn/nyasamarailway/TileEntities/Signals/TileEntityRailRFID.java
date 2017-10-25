package club.nsdn.nyasamarailway.TileEntities.Signals;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public class TileEntityRailRFID extends TileEntityRailReceiver {

    public int P = 0;
    public int R = 10;
    public boolean state = false;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        P = tagCompound.getInteger("P");
        R = tagCompound.getInteger("R");
        state = tagCompound.getBoolean("state");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("P", P);
        tagCompound.setInteger("R", R);
        tagCompound.setBoolean("state", state);
        return super.toNBT(tagCompound);
    }

}
