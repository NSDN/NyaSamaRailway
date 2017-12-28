package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailSniffer;
import club.nsdn.nyasamatelecom.api.network.ParticlePacket;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
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
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
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

        funcList.replace("nya", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            if (dst.data.toString().isEmpty()) return Result.ERR;

            String type = dst.data.toString();
            double tX, tY, tZ;
            if (regGroup[0].type != RegType.STR)
                tX = Double.valueOf(regGroup[0].data.toString());
            else tX = 0;
            if (regGroup[1].type != RegType.STR)
                tY = Double.valueOf(regGroup[1].data.toString());
            else tY = 0;
            if (regGroup[2].type != RegType.STR)
                tZ = Double.valueOf(regGroup[2].data.toString());
            else tZ = 0;
            if (getPlayer() != null)
                new ParticlePacket(
                        type, getX(), getY(), getZ(), tX, tY, tZ
                ).send(
                        NetworkWrapper.instance, getPlayer().dimension
                );
            else if (getCart() != null)
                new ParticlePacket(
                        type, getX(), getY(), getZ(), tX, tY, tZ
                ).send(
                        NetworkWrapper.instance, getCart().dimension
                );

            return Result.OK;
        }));

        funcList.replace("nyaa", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            if (dst.data.toString().isEmpty()) return Result.ERR;

            String type = dst.data.toString();
            double x, y, z, tX, tY, tZ;
            if (regGroup[0].type != RegType.STR)
                x = Double.valueOf(regGroup[0].data.toString());
            else x = 0;
            if (regGroup[1].type != RegType.STR)
                y = Double.valueOf(regGroup[1].data.toString());
            else y = 0;
            if (regGroup[2].type != RegType.STR)
                z = Double.valueOf(regGroup[2].data.toString());
            else z = 0;
            if (regGroup[3].type != RegType.STR)
                tX = Double.valueOf(regGroup[3].data.toString());
            else tX = 0;
            if (regGroup[4].type != RegType.STR)
                tY = Double.valueOf(regGroup[4].data.toString());
            else tY = 0;
            if (regGroup[5].type != RegType.STR)
                tZ = Double.valueOf(regGroup[5].data.toString());
            else tZ = 0;
            if (getPlayer() != null)
                new ParticlePacket(
                        type, getX(), getY(), getZ(), tX, tY, tZ
                ).send(
                        NetworkWrapper.instance, getPlayer().dimension
                );
            else if (getCart() != null)
                new ParticlePacket(
                        type, getX(), getY(), getZ(), tX, tY, tZ
                ).send(
                        NetworkWrapper.instance, getCart().dimension
                );

            return Result.OK;
        }));

    }

    public abstract TileEntityRailSniffer getRail();
    public abstract EntityMinecart getCart();

}
