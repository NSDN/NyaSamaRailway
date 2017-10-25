package club.nsdn.nyasamarailway.Util;

import cn.ac.nya.nsasm.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2016.9.20.
 */
public abstract class NSASM extends cn.ac.nya.nsasm.NSASM {

    public static String[][] getCode(NBTTagList list) {
        String codeBuf = "";
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++)
                codeBuf = codeBuf.concat(list.getStringTagAt(i) + "\n");
        } else {
            codeBuf = "prt \"Code is Empty!\"\n";
        }

        return Util.getSegments(codeBuf);
    }

    public static String getCodeString(NBTTagList list) {
        String codeBuf = "";
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++)
                codeBuf = codeBuf.concat(list.getStringTagAt(i) + "\n");
        } else {
            codeBuf = "prt \"Code is Empty!\"\n";
        }

        return codeBuf;
    }

    public NSASM(String[][] code) {
        super(64, 32, 32, code);
    }

    public NSASM(String code) {
        super(64, 32, 32, Util.getSegments(code));
    }

    @Override
    protected void loadFunList() {
        super.loadFunList();

        funList.replace("prt", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (getPlayer() == null) return Result.OK;
            if (dst.type == RegType.STR) {
                getPlayer().addChatComponentMessage(new ChatComponentText(((String) dst.data).substring(dst.strPtr)));
            } else getPlayer().addChatComponentMessage(new ChatComponentText(dst.data.toString()));
            return Result.OK;
        }));

        funList.put("nya", ((dst, src) -> {
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
            if (getWorld() != null)
                getWorld().spawnParticle(type, getX(), getY(), getZ(), tX, tY, tZ);
            return Result.OK;
        }));

        loadFunc(funList);
    }

    public abstract World getWorld();
    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();
    public abstract EntityPlayer getPlayer();

    public abstract void loadFunc(LinkedHashMap<String, Operator> funcList);

}
