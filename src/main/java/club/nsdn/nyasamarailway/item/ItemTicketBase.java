package club.nsdn.nyasamarailway.item;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class ItemTicketBase extends Item {

    public ItemTicketBase(String name, String icon) {
        super();
        setMaxStackSize(1);
        setUnlocalizedName(name);
        setTextureName("nyasamarailway" + ":" + icon);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    public static void setOver(ItemStack itemStack, int over) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        tagCompound.setInteger("over", over);
        tagCompound.setBoolean("state", false);
        itemStack.setTagCompound(tagCompound);
    }

    public static void setState(ItemStack itemStack, boolean state) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        tagCompound.setBoolean("state", state);
        itemStack.setTagCompound(tagCompound);
    }

    public static int getOver(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        return tagCompound.getInteger("over");
    }

    public static boolean getState(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        return tagCompound.getBoolean("state");
    }

    public static void decOver(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        int over = tagCompound.getInteger("over");
        if (over > 0) over -= 1;
        tagCompound.setInteger("over", over);
        itemStack.setTagCompound(tagCompound);
    }

}
