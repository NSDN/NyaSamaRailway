package club.nsdn.nyasamarailway.item.misc;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class ItemTicketBase extends Item {

    public ItemTicketBase(String name, String id) {
        super();
        setMaxStackSize(1);
        setUnlocalizedName(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public static void setOver(ItemStack itemStack, int over) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) return;
        tagCompound.setInteger("over", over);
        tagCompound.setBoolean("state", false);
        itemStack.setTagCompound(tagCompound);
    }

    public static void setState(ItemStack itemStack, boolean state) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) return;
        tagCompound.setBoolean("state", state);
        itemStack.setTagCompound(tagCompound);
    }

    public static int getOver(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) return -1;
        return tagCompound.getInteger("over");
    }

    public static boolean getState(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) return false;
        return tagCompound.getBoolean("state");
    }

    public static void decOver(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) return;
        int over = tagCompound.getInteger("over");
        if (over > 0) over -= 1;
        tagCompound.setInteger("over", over);
        itemStack.setTagCompound(tagCompound);
    }

}
