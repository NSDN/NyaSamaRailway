package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityPassiveReceiver;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2017.12.10.
 */
public class TileEntitySignalLight extends TileEntityPassiveReceiver {

    public String lightType = "red&green";
    public String prevLightType = "null";
    public boolean isBlinking = false;
    public boolean isPowered = false;

    public int delay;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        lightType = tagCompound.getString("lightType");
        isBlinking = tagCompound.getBoolean("isBlinking");
        isPowered = tagCompound.getBoolean("isPowered");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("lightType", lightType);
        tagCompound.setBoolean("isBlinking", isBlinking);
        tagCompound.setBoolean("isPowered", isPowered);
        return super.toNBT(tagCompound);
    }

}
