package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public class TileEntityRailRFID extends TileEntityReceiver {

    public int P = 0;
    public int R = 10;
    public double vel = 0;
    public boolean state = false;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        P = tagCompound.getInteger("P");
        R = tagCompound.getInteger("R");
        vel = tagCompound.getDouble("vel");
        state = tagCompound.getBoolean("state");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("P", P);
        tagCompound.setInteger("R", R);
        tagCompound.setDouble("vel", vel);
        tagCompound.setBoolean("state", state);
        return super.toNBT(tagCompound);
    }

}
