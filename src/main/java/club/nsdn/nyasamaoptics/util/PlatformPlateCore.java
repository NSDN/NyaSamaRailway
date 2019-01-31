package club.nsdn.nyasamaoptics.util;

import club.nsdn.nyasamaoptics.network.NetworkWrapper;
import club.nsdn.nyasamaoptics.tileblock.screen.TileEntityPlatformPlate;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public abstract class PlatformPlateCore extends NSASM {

    public PlatformPlateCore(String[][] code) {
        super(code);
    }

    @Override
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
    }

    public void setContent(TileEntityPlatformPlate text, String value) {
        text.content = value;
        TileEntityPlatformPlate.updateThis(text);
    }

    public void setColor(TileEntityPlatformPlate text, int value) {
        text.color = value & 0xFFFFFF;
        TileEntityPlatformPlate.updateThis(text);
    }

    public void setScale(TileEntityPlatformPlate text, float value) {
        text.scale = value;
        TileEntityPlatformPlate.updateThis(text);
    }

    public void setAlign(TileEntityPlatformPlate text, String value) {
        if (value.toLowerCase().equals("center"))
            text.align = TileEntityPlatformPlate.ALIGN_CENTER;
        else if (value.toLowerCase().equals("left"))
            text.align = TileEntityPlatformPlate.ALIGN_LEFT;
        else if (value.toLowerCase().equals("right"))
            text.align = TileEntityPlatformPlate.ALIGN_RIGHT;
        else
            text.align = TileEntityPlatformPlate.ALIGN_CENTER;
        TileEntityPlatformPlate.updateThis(text);
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
        funcList.put("aln", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                setAlign(getTile(), dst.data.toString());
                return Result.OK;
            }
            return Result.ERR;
        }));
    }

    public abstract TileEntityPlatformPlate getTile();

}
