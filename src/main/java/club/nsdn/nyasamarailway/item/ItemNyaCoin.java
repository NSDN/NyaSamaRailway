package club.nsdn.nyasamarailway.item;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.9.5.
 */
public class ItemNyaCoin extends Item {

    public ItemNyaCoin() {
        super();
        setMaxStackSize(1);
        setUnlocalizedName("ItemNyaCoin");
        setTextureName("nyasamarailway" + ":" + "item_nyacoin");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    public static void setValue(ItemStack itemStack, int value) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        tagCompound.setInteger("value", value);
        itemStack.setTagCompound(tagCompound);
    }

    public static int getValue(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        return tagCompound.getInteger("value");
    }

}
