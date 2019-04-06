package club.nsdn.nyasamarailway.api.signal;

import club.nsdn.nyasamatelecom.api.device.SignalBox;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TileEntitySignalLight extends TileEntityReceiver {

    public String lightType = "red&green";
    public String prevLightType = "null";
    public boolean isBlinking = false;
    public boolean isPowered = false;

    public int delay;

    public static void registerController() {
        SignalBox.TileEntitySignalBox.CONTROL_FUNCS.add((tileEntity, state) -> {
            if (tileEntity instanceof TileEntitySignalLight) {
                ((TileEntitySignalLight) tileEntity).isPowered = state;
            }
        });
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);

        lightType = tagCompound.getString("lightType");
        isBlinking = tagCompound.getBoolean("isBlinking");
        isPowered = tagCompound.getBoolean("isPowered");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("lightType", lightType);
        tagCompound.setBoolean("isBlinking", isBlinking);
        tagCompound.setBoolean("isPowered", isPowered);

        return super.toNBT(tagCompound);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(4, 4, 4);
    }

}
