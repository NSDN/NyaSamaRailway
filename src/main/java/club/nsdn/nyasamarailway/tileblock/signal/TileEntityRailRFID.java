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
    public boolean high = false;
    public boolean state = false;

    public String cartSide = "null", cartStr = "null", cartJet = "null";

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        P = tagCompound.getInteger("P");
        R = tagCompound.getInteger("R");
        vel = tagCompound.getDouble("vel");
        high = tagCompound.getBoolean("high");
        state = tagCompound.getBoolean("state");

        cartSide = tagCompound.getString("cartSide");
        cartStr = tagCompound.getString("cartStr");
        cartJet = tagCompound.getString("cartJet");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("P", P);
        tagCompound.setInteger("R", R);
        tagCompound.setDouble("vel", vel);
        tagCompound.setBoolean("high", high);
        tagCompound.setBoolean("state", state);

        tagCompound.setString("cartSide", cartSide);
        tagCompound.setString("cartStr", cartStr);
        tagCompound.setString("cartJet", cartJet);
        return super.toNBT(tagCompound);
    }

}
