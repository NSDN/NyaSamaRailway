package club.nsdn.nyasamarailway.Util;

import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagList;

/**
 * Created by drzzm32 on 2016.9.20.
 */
public class Util {

    public static NBTTagList getTagListFromBook(ItemStack itemStack) {
        if (itemStack == null) return null;
        if (itemStack.getItem() instanceof ItemWritableBook ||
                itemStack.getItem() instanceof ItemEditableBook) {
            if (!itemStack.hasTagCompound()) return null;
            return itemStack.getTagCompound().getTagList("pages", 8);
        }
        return null;
    }

    public static boolean loadIf() {
        return Loader.isModLoaded("NyaSamaTelecom");
    }

}
