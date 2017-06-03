package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class ItemTrainController8Bit extends ItemToolBase {

    public ItemTrainController8Bit() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemTrainController8Bit");
        setRegistryName(NyaSamaRailway.modid, getRegisterID());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        player.swingArm(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean advInfo) {
        info.add(new TextComponentTranslation("info.ntp8.1").getFormattedText());
        info.add(new TextComponentTranslation("info.ntp8.2").getFormattedText());
        info.add(new TextComponentTranslation("info.ntp8.3").getFormattedText());
    }

    @Override
    public String getRegisterID() {
        return "ntp-8";
    }
}
