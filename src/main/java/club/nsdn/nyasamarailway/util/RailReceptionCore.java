package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.util.ChatComponentTranslation;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2018.4.6.
 */
public abstract class RailReceptionCore extends NSASM {

    public RailReceptionCore(String[][] code) {
        super(code);
    }

    @Override
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
    }

    private void prt(String format, Object... args) {
        if (getPlayer() != null)
            getPlayer().addChatComponentMessage(new ChatComponentTranslation(format, args));
    }

    public void setDelay(TileEntityRailReception rail, int value) {
        if (rail == null) return;
        rail.setDelay = value <= 0 ? 1 : value;
        prt("info.reception.set", rail.setDelay);
    }

    @Override
    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
        funcList.put("set", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setDelay(getRail(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));

    }

    public abstract TileEntityRailReception getRail();

}