package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.thewdj.telecom.IPassive;

/**
 * Created by drzzm32 on 2018.4.6.
 */
public class TileEntityRailReception extends TileEntityActuator {

    public static final int SPAWN_DELAY = 10;

    public int delay = 0;
    public int count = 0;
    public boolean enable = false;
    public boolean prev = false;
    public boolean doorCtrl = false;

    public String cartType = "";
    public String extInfo = "";
    public int setDelay = 10;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        cartType = tagCompound.getString("cartType");
        extInfo = tagCompound.getString("extInfo");
        setDelay = tagCompound.getInteger("setDelay");
        if (setDelay == 0) setDelay = 10; // for old devices
        doorCtrl = tagCompound.getBoolean("doorCtrl");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("cartType", cartType);
        tagCompound.setString("extInfo", extInfo);
        tagCompound.setInteger("setDelay", setDelay);
        tagCompound.setBoolean("doorCtrl", doorCtrl);
        return super.toNBT(tagCompound);
    }

    @Override
    public void controlTarget(boolean state) {
        if (!setTargetSender(state)) {
            if (!setTargetGetter(state)) {
                if (getTarget() != null) {
                    TileEntity tileEntity = getTarget();
                    if (tileEntity instanceof TileEntityReceiver) {
                        if (tileEntity instanceof IPassive) {
                            super.controlTarget(state);
                        }
                    } else {
                        super.controlTarget(state);
                    }
                }
            }
        }
    }

    public boolean setTargetSender(boolean state) {
        TileEntity target = getTarget();
        if (target == null) return false;

        if (target instanceof SignalBoxSender.TileEntitySignalBoxSender) {
            ((SignalBoxSender.TileEntitySignalBoxSender) target).isEnabled = state;
            return true;
        }
        return false;
    }

    public boolean setTargetGetter(boolean state) {
        TileEntity target = getTarget();
        if (target == null) return false;

        if (target instanceof SignalBoxGetter.TileEntitySignalBoxGetter) {
            ((SignalBoxGetter.TileEntitySignalBoxGetter) target).isEnabled = state;
            return true;
        }
        return false;
    }

    public void reset() {
        count = 0;
        delay = 0;
        doorCtrl = false;
        enable = false;
    }

}
