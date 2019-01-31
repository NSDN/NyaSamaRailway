package club.nsdn.nyasamaoptics.util;

import club.nsdn.nyasamaoptics.network.NetworkWrapper;
import club.nsdn.nyasamaoptics.tileblock.holo.PillarHead;
import club.nsdn.nyasamaoptics.util.font.FontLoader;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public abstract class PillarHeadCore extends NSASM {

    public PillarHeadCore(String[][] code) {
        super(code);
    }

    @Override
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
    }

    public void setContent(PillarHead.TileEntityPillarHead text, String value) {
        text.content = value;
        PillarHead.TileEntityPillarHead.updateThis(text);
    }

    public void setColor(PillarHead.TileEntityPillarHead text, int value) {
        text.color = value & 0xFFFFFF;
        PillarHead.TileEntityPillarHead.updateThis(text);
    }

    public void setThick(PillarHead.TileEntityPillarHead text, int value) {
        text.thick = value;
        PillarHead.TileEntityPillarHead.updateThis(text);
    }

    public void setScale(PillarHead.TileEntityPillarHead text, float value) {
        text.scale = value;
        PillarHead.TileEntityPillarHead.updateThis(text);
    }

    public void setFont(PillarHead.TileEntityPillarHead text, String value) {
        if (value.toLowerCase().equals("kai"))
            text.font = FontLoader.FONT_KAI;
        else if (value.toLowerCase().equals("hei"))
            text.font = FontLoader.FONT_HEI;
        else if (value.toLowerCase().equals("long"))
            text.font = FontLoader.FONT_LONG;
        else if (value.toLowerCase().equals("song"))
            text.font = FontLoader.FONT_SONG;
        else if (value.toLowerCase().equals("lishu"))
            text.font = FontLoader.FONT_LISHU;
        else
            text.font = FontLoader.FONT_SONG;
        PillarHead.TileEntityPillarHead.updateThis(text);
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
        funcList.put("thk", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setThick(getTile(), (int) dst.data);
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
        funcList.put("font", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                setFont(getTile(), dst.data.toString());
                return Result.OK;
            }
            return Result.ERR;
        }));
    }

    public abstract PillarHead.TileEntityPillarHead getTile();

}
