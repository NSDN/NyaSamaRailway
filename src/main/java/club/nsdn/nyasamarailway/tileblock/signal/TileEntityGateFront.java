package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGateFront extends TileEntityReceiver {

    public static final int DELAY = 5;
    public int delay;

    public int over = -1;
    public boolean isEnabled = true;
    public ForgeDirection direction;

    public int setOver = 1;

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB
                .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                .expand(4, 4, 4);
    }

    public void fromNBT(NBTTagCompound tagCompound) {
        over = tagCompound.getInteger("over");
        isEnabled = tagCompound.getBoolean("isEnabled");
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );
        setOver = tagCompound.getInteger("setOver");
        super.fromNBT(tagCompound);
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("over", over);
        tagCompound.setBoolean("isEnabled", isEnabled);
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());
        tagCompound.setInteger("setOver", setOver);
        return super.toNBT(tagCompound);
    }

}
