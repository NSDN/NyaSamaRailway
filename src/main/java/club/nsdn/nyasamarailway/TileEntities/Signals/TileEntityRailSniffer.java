package club.nsdn.nyasamarailway.TileEntities.Signals;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public class TileEntityRailSniffer extends TileEntityRailTransceiver {

    public static final boolean NSASM_IDLE = false;
    public static final boolean NSASM_DONE = true;
    public boolean nsasmState = NSASM_IDLE;
    public String nsasmCode = "";

    public boolean enable = false;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        nsasmState = tagCompound.getBoolean("nsasmState");
        nsasmCode = tagCompound.getString("nsasmCode");
        enable = tagCompound.getBoolean("enable");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setBoolean("nsasmState", nsasmState);
        tagCompound.setString("nsasmCode", nsasmCode);
        tagCompound.setBoolean("enable", enable);
        return super.toNBT(tagCompound);
    }

}
