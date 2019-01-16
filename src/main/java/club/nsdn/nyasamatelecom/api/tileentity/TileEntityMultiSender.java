package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TileEntityMultiSender extends TileEntityTransceiver {

    public int targetCount;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);
        targetCount = tagCompound.getInteger("targetCount");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("targetCount", targetCount);
        return super.toNBT(tagCompound);
    }

    public void incTarget() {
        targetCount += 1;
    }

    public void decTarget() {
        targetCount = targetCount > 0 ? targetCount - 1 : 0;
    }

    public boolean setMetadata(int meta) {
        this.META = meta;
        update();
        return true;
    }

    public TileEntityMultiSender() {
        targetCount = 0;
    }

}
