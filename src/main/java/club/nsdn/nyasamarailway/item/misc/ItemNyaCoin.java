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
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNyaCoin extends Item {

    public ItemNyaCoin() {
        super();
        setMaxStackSize(1);
        setUnlocalizedName("ItemNyaCoin");
        setRegistryName(NyaSamaRailway.MODID, "item_nyacoin");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public static void setValue(ItemStack itemStack, int value) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) return;
        tagCompound.setInteger("value", value);
        itemStack.setTagCompound(tagCompound);
    }

    public static int getValue(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) return -1;
        return tagCompound.getInteger("value");
    }

}
