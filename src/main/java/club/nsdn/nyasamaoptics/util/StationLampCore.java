package club.nsdn.nyasamaoptics.util;

import club.nsdn.nyasamaoptics.network.NetworkWrapper;
import club.nsdn.nyasamaoptics.tileblock.screen.StationLamp;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public abstract class StationLampCore extends NSASM {

    public StationLampCore(String[][] code) {
        super(code);
    }

    @Override
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
    }

    public void setContent(StationLamp.TileEntityStationLamp lamp, String value) {
        lamp.content = value;
        StationLamp.TileEntityStationLamp.updateThis(lamp);
    }

    public void setColor(StationLamp.TileEntityStationLamp lamp, int value) {
        lamp.color = value & 0xFFFFFF;
        StationLamp.TileEntityStationLamp.updateThis(lamp);
    }

    public void setBack(StationLamp.TileEntityStationLamp lamp, int value) {
        lamp.back = value;
        StationLamp.TileEntityStationLamp.updateThis(lamp);
    }

    public void setScale(StationLamp.TileEntityStationLamp lamp, float value) {
        lamp.scale = value;
        StationLamp.TileEntityStationLamp.updateThis(lamp);
    }

    public void setLogo(StationLamp.TileEntityStationLamp lamp, String value) {
        lamp.logo = value;
        StationLamp.TileEntityStationLamp.updateThis(lamp);
    }

    @Override
    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
        funcList.put("show", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                setContent(getTile(), dst.data.toString());
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("clr", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setColor(getTile(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("fore", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setColor(getTile(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("back", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setBack(getTile(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("scl", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type != RegType.STR) {
                if (dst.type == RegType.FLOAT)
                    setScale(getTile(), (float) dst.data);
                else if (dst.type == RegType.INT)
                    setScale(getTile(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("logo", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                setLogo(getTile(), dst.data.toString());
                getPlayer().sendMessage(new TextComponentString(
                    "[NSO] Logo -> " + getTile().logo
                ));
                return Result.OK;
            }
            return Result.ERR;
        }));
    }

    public abstract StationLamp.TileEntityStationLamp getTile();

}
