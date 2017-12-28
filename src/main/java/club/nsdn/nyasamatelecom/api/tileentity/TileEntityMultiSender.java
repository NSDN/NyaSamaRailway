package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntityMultiSender extends TileEntityTransceiver {

    public int targetCount;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        targetCount = tagCompound.getInteger("targetCount");
        super.fromNBT(tagCompound);
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
        this.getWorldObj().setBlockMetadataWithNotify(
                this.xCoord, this.yCoord, this.zCoord, meta, 3
        );
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    public TileEntityMultiSender() {
        targetCount = 0;
    }

}
