package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.Util.GuiNGT;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public class ItemNGT extends ItemWritableBook {

    public ItemNGT() {
        super();
        setUnlocalizedName("ItemNGT");
        setMaxStackSize(1);
        setTextureName("nyasamarailway" + ":" + "item_ngt");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNGT(player, itemStack));
        }
        return itemStack;
    }

}
