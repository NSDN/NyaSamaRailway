package club.nsdn.nyasamarailway.Util;

import cn.ac.nya.nsasm.Util;
import net.minecraft.nbt.NBTTagList;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2016.9.20.
 */
public class NSASM extends cn.ac.nya.nsasm.NSASM {

    public interface IFuncLoader {
        void load(LinkedHashMap<String, Operator> funcList);
    }

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

    public NSASM(String[][] code, IFuncLoader loader) {
        super(64, 32, 32, code);
        loader.load(funList);
    }

}
