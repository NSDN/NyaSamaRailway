package club.nsdn.nyasamarailway.Util;

import club.nsdn.nyasamarailway.Items.ItemNGT;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

/**
 * Created by drzzm32 on 2016.9.20.
 */
public class Util {

    public static NBTTagList getTagListFromNGT(ItemStack itemStack) {
        if (itemStack == null) return null;
        if (itemStack.getItem() instanceof ItemNGT) {
            if (!itemStack.hasTagCompound()) return null;
            return itemStack.getTagCompound().getTagList("pages", 8);
        }
        return null;
    }

}
