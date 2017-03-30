package club.nsdn.nyasamarailway.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.9.
 */
public class ItemTrainController8Bit extends ItemToolBase {

    public ItemTrainController8Bit() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemTrainController8Bit");
        setTexName("ntp-8");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }
}
