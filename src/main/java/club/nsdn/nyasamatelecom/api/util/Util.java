package club.nsdn.nyasamatelecom.api.util;

import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class Util {

    public static NBTTagList getTagListFromNGT(ItemStack itemStack) {
        if (itemStack == null) return null;
        if (itemStack.getItem() instanceof NGTablet) {
            if (!itemStack.hasTagCompound()) return null;
            return itemStack.getTagCompound().getTagList("pages", 8);
        }
        return null;
    }

    public static void say(EntityPlayer player, String format, Object... args) {
        if (player == null) return;
        if (player instanceof EntityPlayerMP)
            player.sendMessage(new TextComponentTranslation(format, args));
    }

}
