package club.nsdn.nyasamarailway.ExtMod;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm on 2016.11.27.
 */
public class Util {

    public static boolean verifyClass(Class<?> c, String tag, Class<?> end) {
        if (c == end) return false;
        if (c.getSuperclass().getName().contains(tag))
            return true;
        return verifyClass(c.getSuperclass(), tag, end);
    }

    public static void modifyNBT(Entity entity, String nbtTag, Object value) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        entity.writeToNBT(tagCompound);
        if (value instanceof Boolean)
            tagCompound.setBoolean(nbtTag, (Boolean) value);
        else if (value instanceof NBTBase)
            tagCompound.setTag(nbtTag, (NBTBase) value);
        else if (value instanceof Integer)
            tagCompound.setInteger(nbtTag, (Integer) value);
        else if (value instanceof String)
            tagCompound.setString(nbtTag, (String) value);
        else if (value instanceof Double)
            tagCompound.setDouble(nbtTag, (Double) value);
        else if (value instanceof Float)
            tagCompound.setFloat(nbtTag, (Float) value);
        else if (value instanceof Byte)
            tagCompound.setByte(nbtTag, (Byte) value);
        entity.readFromNBT(tagCompound);
    }

}
