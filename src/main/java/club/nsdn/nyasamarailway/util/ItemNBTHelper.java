package club.nsdn.nyasamarailway.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by drzzm32 on 2018.3.7.
 */
public class ItemNBTHelper<T> {

    private Class<T> cls;
    private String nbtTag;

    public ItemNBTHelper(Class<T> cls, String nbtTag) {
        this.cls = cls;
        this.nbtTag = nbtTag;
    }

    public void set(ItemStack itemStack, T value) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        try {
            Object obj = (Object) value;
            if (tagCompound == null) return;
            if (cls.equals(Integer.class)) tagCompound.setInteger(nbtTag, (int) obj);
            else if (cls.equals(int[].class)) tagCompound.setIntArray(nbtTag, (int[]) obj);
            else if (cls.equals(String.class)) tagCompound.setString(nbtTag, (String) obj);
            else if (cls.equals(Boolean.class)) tagCompound.setBoolean(nbtTag, (Boolean) obj);
        } catch (Exception e) {
            return;
        }
        itemStack.setTagCompound(tagCompound);
    }

    public T get(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        try {
            T t = null;
            if (tagCompound == null) return null;
            if (cls.equals(Integer.class)) t = cls.cast(tagCompound.getInteger(nbtTag));
            else if (cls.equals(int[].class)) t = cls.cast(tagCompound.getIntArray(nbtTag));
            else if (cls.equals(String.class)) t = cls.cast(tagCompound.getString(nbtTag));
            else if (cls.equals(Boolean.class)) t = cls.cast(tagCompound.getBoolean(nbtTag));
            return t;
        } catch (Exception e) {
            return null;
        }
    }

}
