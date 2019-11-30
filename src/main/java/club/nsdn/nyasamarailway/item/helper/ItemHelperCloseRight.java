package club.nsdn.nyasamarailway.item.helper;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.api.tileentity.*;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.11.20
 */
public class ItemHelperCloseRight extends ToolBase {

    public ItemHelperCloseRight() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemHelperCloseRight");
        setRegistryName(NyaSamaRailway.MODID, "helper_close_right");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == Blocks.AIR)
            return EnumActionResult.FAIL;

        if (player.isSneaking()) {
            Util.say(player, "info.PierBuilder.begin");

            facing = player.getHorizontalFacing();
            pos = pos.up();

            ItemStack stack = player.getHeldItem(EnumHand.OFF_HAND);
            if (stack.getItem() instanceof NGTablet) {
                double speed;

                String str = NSASM.getCodeString(Util.getTagListFromNGT(stack));
                str = str.split("\n")[0];
                if (str.contains(".")) {
                    try {
                        speed = Double.parseDouble(str); // m/tick
                    } catch (Exception e) {
                        speed = -0.6;
                    }
                } else {
                    try {
                        speed = Integer.parseInt(str); // km/h
                    } catch (Exception e) {
                        speed = -45;
                    }
                    speed = speed / 3.6 / 20;
                }

                boolean high = Math.abs(speed) >= 1.2;
                int power = 10;
                if (high) {
                    if (speed >= 1.5)
                        power += 5;
                    if (speed >= 2.0)
                        power += 5;
                } else {
                    if (speed >= 0.8)
                        power += 5;
                    if (speed >= 1.0)
                        power += 5;
                }

                Util.say(player, "[NSR] P: " + power + ", V: " + String.format("%1.2f", speed));

                TileEntityTransceiver source = DeployHelper.INSTANCE.placeSnifferHs(world, pos, player, false, true);
                TileEntityReceiver target = DeployHelper.INSTANCE.placeRFIDHs(world, pos.offset(facing), player, power, 0, speed, high, true, true, true, true);
                DeployHelper.INSTANCE.connect(source, target);
            } else {
                TileEntityTransceiver source = DeployHelper.INSTANCE.placeSniffer(world, pos, player, true);
                TileEntityReceiver target = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facing), player, 2, 0, 0.2, true, true, true);
                DeployHelper.INSTANCE.connect(source, target);
            }

            Util.say(player, "info.PierBuilder.finish");
        }

        return world.isRemote ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
    }

}
