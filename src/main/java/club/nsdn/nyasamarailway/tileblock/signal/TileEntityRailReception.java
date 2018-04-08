package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2018.4.6.
 */
public class TileEntityRailReception extends TileEntityReceiver {

    public int delay = 0;
    public int count = 0;
    public boolean enable = false;
    public boolean prev = false;

    public String cartType = "";
    public String extInfo = "";
    public int setDelay = 10;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        cartType = tagCompound.getString("cartType");
        extInfo = tagCompound.getString("extInfo");
        setDelay = tagCompound.getInteger("setDelay");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("cartType", cartType);
        tagCompound.setString("extInfo", extInfo);
        tagCompound.setInteger("setDelay", setDelay);
        return super.toNBT(tagCompound);
    }

}
