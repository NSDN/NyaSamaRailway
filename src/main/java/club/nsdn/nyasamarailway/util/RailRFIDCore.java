package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailRFID;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public abstract class RailRFIDCore extends NSASM {

    public RailRFIDCore(String[][] code) {
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

    public void setPower(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        if (rfid == null) return;
        rfid.P = value > 20 ? 20 : (value < 0 ? 0 : value);
        prt("info.rfid.pwr", rfid.P);
    }

    public void setBrake(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        if (rfid == null) return;
        value = 10 - value;
        rfid.R = value > 10 ? 10 : (value < 1 ? 1 : value);
        prt("info.rfid.brk", 10 - rfid.R);
    }

    public void setState(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        if (rfid == null) return;
        rfid.state = value > 0;
        prt("info.rfid.ste", rfid.state ? 1 : 0);
    }

    public void setVelocity(TileEntityRailRFID rfid, EntityPlayer player, double value) {
        if (rfid == null) return;
        rfid.vel = value;
        prt("info.rfid.vel", rfid.vel);
    }

    @Override
    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
        funcList.put("pwr", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setPower(getRFID(), getPlayer(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("brk", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setBrake(getRFID(), getPlayer(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("ste", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setState(getRFID(), getPlayer(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("vel", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.FLOAT || dst.type == RegType.INT) {
                setVelocity(getRFID(), getPlayer(), Double.valueOf(dst.data.toString()));
                return Result.OK;
            }
            return Result.ERR;
        }));

        funcList.put("side", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                if (getRFID() != null) {
                    getRFID().cartSide = dst.data.toString();
                    prt("info.rfid.side", dst.data.toString());
                }
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("jets", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                if (getRFID() != null) {
                    getRFID().cartStr = dst.data.toString();
                    prt("info.rfid.str", dst.data.toString());
                }
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("jet", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                if (getRFID() != null) {
                    getRFID().cartJet = dst.data.toString();
                    prt("info.rfid.jet", dst.data.toString());
                }
                return Result.OK;
            }
            return Result.ERR;
        }));

    }

    public abstract TileEntityRailRFID getRFID();

}
