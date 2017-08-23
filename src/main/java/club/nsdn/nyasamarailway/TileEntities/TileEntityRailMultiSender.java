package club.nsdn.nyasamarailway.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2017.8.12.
 */
public class TileEntityRailMultiSender extends TileEntityRailTransceiver {

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

    public TileEntityRailMultiSender() {
        targetCount = 0;
    }

}
