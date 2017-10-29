package club.nsdn.nyasamarailway.Util;

import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailSniffer;
import net.minecraft.entity.item.EntityMinecart;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public abstract class RailSnifferCore extends NSASM {

    public RailSnifferCore(String code) {
        super(code);
    }

    @Override
    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
        funcList.put("keep", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type == RegType.STR) return Result.ERR;

            if (getRail() != null)
                getRail().keep = Integer.valueOf(dst.data.toString());
            return Result.OK;
        }));
        funcList.put("enb", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst != null) return Result.ERR;

            if (getRail() != null)
                getRail().enable = true;
            return Result.OK;
        }));
        funcList.put("rnd", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;

            dst.type = RegType.INT;
            dst.data = Math.round(Math.random() * 255);
            return Result.OK;
        }));
        funcList.put("equ", ((dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (src.type != RegType.STR) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            if (src.data.toString().isEmpty()) return Result.ERR;
            if (dst.data.toString().isEmpty()) return Result.ERR;

            if (dst.data.toString().equals(src.data.toString())) {
                funcList.get("push").run(regGroup[0], null);
                funcList.get("push").run(regGroup[1], null);
                regGroup[0].type = RegType.INT;
                regGroup[0].data = 0;
                regGroup[1].type = RegType.INT;
                regGroup[1].data = 0;
                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                funcList.get("pop").run(regGroup[1], null);
                funcList.get("pop").run(regGroup[0], null);
            } else {
                funcList.get("push").run(regGroup[0], null);
                funcList.get("push").run(regGroup[1], null);
                regGroup[0].type = RegType.INT;
                regGroup[0].data = 1;
                regGroup[1].type = RegType.INT;
                regGroup[1].data = 0;
                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                funcList.get("pop").run(regGroup[1], null);
                funcList.get("pop").run(regGroup[0], null);
            }
            return Result.OK;
        }));
        funcList.put("ctn", ((dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (src.type != RegType.STR) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            if (src.data.toString().isEmpty()) return Result.ERR;
            if (dst.data.toString().isEmpty()) return Result.ERR;

            if (dst.data.toString().contains(src.data.toString())) {
                funcList.get("push").run(regGroup[0], null);
                funcList.get("push").run(regGroup[1], null);
                regGroup[0].type = RegType.INT;
                regGroup[0].data = 0;
                regGroup[1].type = RegType.INT;
                regGroup[1].data = 0;
                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                funcList.get("pop").run(regGroup[1], null);
                funcList.get("pop").run(regGroup[0], null);
            } else {
                funcList.get("push").run(regGroup[0], null);
                funcList.get("push").run(regGroup[1], null);
                regGroup[0].type = RegType.INT;
                regGroup[0].data = 1;
                regGroup[1].type = RegType.INT;
                regGroup[1].data = 0;
                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                funcList.get("pop").run(regGroup[1], null);
                funcList.get("pop").run(regGroup[0], null);
            }
            return Result.OK;
        }));
        funcList.put("sum", ((dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (src.type != RegType.STR) return Result.ERR;
            if (src.data.toString().isEmpty()) return Result.ERR;
            if (dst.readOnly) return Result.ERR;

            dst.type = RegType.INT;
            dst.data = 0;
            for (char c : src.data.toString().toCharArray())
                dst.data = (int) dst.data + (int) c;
            return Result.OK;
        }));
        funcList.put("cid", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;

            dst.type = RegType.STR;
            if (getCart() == null) dst.data = "null";
            else dst.data = getCart().getCommandSenderName();
            return Result.OK;
        }));
        funcList.put("pid", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;

            dst.type = RegType.STR;
            if (getPlayer() == null) dst.data = "null";
            else dst.data = getPlayer().getDisplayName();
            return Result.OK;
        }));

    }

    public abstract TileEntityRailSniffer getRail();
    public abstract EntityMinecart getCart();

}
