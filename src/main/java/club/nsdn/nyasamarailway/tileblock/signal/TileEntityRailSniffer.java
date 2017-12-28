package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public class TileEntityRailSniffer extends TileEntityTransceiver {

    public static final boolean NSASM_IDLE = false;
    public static final boolean NSASM_DONE = true;
    public boolean nsasmState = NSASM_IDLE;
    public String nsasmCode = "";

    public boolean enable = false;
    public int keep = 100;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        nsasmState = tagCompound.getBoolean("nsasmState");
        nsasmCode = tagCompound.getString("nsasmCode");
        enable = tagCompound.getBoolean("enable");
        keep = tagCompound.getInteger("keep");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setBoolean("nsasmState", nsasmState);
        tagCompound.setString("nsasmCode", nsasmCode);
        tagCompound.setBoolean("enable", enable);
        tagCompound.setInteger("keep", keep);
        return super.toNBT(tagCompound);
    }

}
