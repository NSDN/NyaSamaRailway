package club.nsdn.nyasamaoptics.item;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.creativetab.CreativeTabLoader;
import club.nsdn.nyasamaoptics.tileblock.holo.TileEntityHoloText;
import club.nsdn.nyasamaoptics.tileblock.light.RGBLight;
import club.nsdn.nyasamaoptics.tileblock.screen.LEDPlate;
import club.nsdn.nyasamaoptics.tileblock.screen.TileEntityPlatformPlate;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class NSOConv extends ToolBase {

    public NSOConv() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("NSOConv");
        setCreativeTab(CreativeTabLoader.tabNyaSamaOptics);
        setRegistryName(NyaSamaOptics.MODID, "nso_conv");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return EnumActionResult.FAIL;

        if (!world.isRemote) {
            if (!player.isSneaking()) {
                if (tileEntity instanceof TileEntityHoloText) {
                    TileEntityHoloText holoText = (TileEntityHoloText) tileEntity;

                    String str = "[NSO] Color: 0x" + Integer.toHexString(holoText.color).toUpperCase();
                    player.sendMessage(new TextComponentString(str));

                    return EnumActionResult.SUCCESS;
                } else if (tileEntity instanceof TileEntityPlatformPlate) {
                    TileEntityPlatformPlate plate = (TileEntityPlatformPlate) tileEntity;

                    String str = "[NSO] Color: 0x" + Integer.toHexString(plate.color).toUpperCase();
                    player.sendMessage(new TextComponentString(str));

                    return EnumActionResult.SUCCESS;
                } else if (tileEntity instanceof RGBLight.TileEntityRGBLight) {
                    RGBLight.TileEntityRGBLight light = (RGBLight.TileEntityRGBLight) tileEntity;

                    String str = "[NSO] Color: 0x" + Integer.toHexString(light.color).toUpperCase();
                    player.sendMessage(new TextComponentString(str));

                    return EnumActionResult.SUCCESS;
                } else if (tileEntity instanceof LEDPlate.TileEntityLEDPlate) {
                    LEDPlate.TileEntityLEDPlate plate = (LEDPlate.TileEntityLEDPlate) tileEntity;

                    String str = "[NSO] Fore: 0x" + Integer.toHexString(plate.color).toUpperCase() +
                            ", Back: 0x" + Integer.toHexString(plate.back).toUpperCase();
                    player.sendMessage(new TextComponentString(str));

                    return EnumActionResult.SUCCESS;
                }
            } else {
                if (tileEntity instanceof TileEntityBase) {
                    TileEntityBase tileEntityBase = (TileEntityBase) tileEntity;

                    String str = "[NSO] META: 0x" + Integer.toHexString(tileEntityBase.META).toUpperCase();
                    player.sendMessage(new TextComponentString(str));

                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return EnumActionResult.FAIL;
    }

}
