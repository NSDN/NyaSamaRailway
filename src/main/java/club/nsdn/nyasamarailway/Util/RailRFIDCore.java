package club.nsdn.nyasamarailway.Util;

import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailRFID;
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

    public void setPower(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        if (rfid == null) return;
        rfid.P = value > 20 ? 20 : (value < 0 ? 0 : value);
        player.addChatComponentMessage(
                new ChatComponentTranslation("info.rfid.pwr", rfid.P));
    }

    public void setBrake(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        if (rfid == null) return;
        value = 10 - value;
        rfid.R = value > 10 ? 10 : (value < 1 ? 1 : value);
        player.addChatComponentMessage(
                new ChatComponentTranslation("info.rfid.brk", 10 - rfid.R));
    }

    public void setState(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        if (rfid == null) return;
        rfid.state = value > 0;
        player.addChatComponentMessage(
                new ChatComponentTranslation("info.rfid.ste", rfid.state ? 1 : 0));
    }

    public void setVelocity(TileEntityRailRFID rfid, EntityPlayer player, double value) {
        if (rfid == null) return;
        rfid.vel = value;
        player.addChatComponentMessage(
                new ChatComponentTranslation("info.rfid.vel", rfid.vel));
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

    }

    public abstract TileEntityRailRFID getRFID();

}
