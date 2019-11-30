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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.11.20
 */
public class ItemHelperReceptionRight extends ToolBase {

    public ItemHelperReceptionRight() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemHelperReceptionRight");
        setRegistryName(NyaSamaRailway.MODID, "helper_reception_right");
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

            double limit = 0.6;
            ItemStack stack = player.getHeldItem(EnumHand.OFF_HAND);
            if (stack.getItem() instanceof NGTablet) {
                String str = NSASM.getCodeString(Util.getTagListFromNGT(stack));
                str = str.split("\n")[0];
                if (str.contains(".")) {
                    try {
                        limit = Double.parseDouble(str); // m/tick
                    } catch (Exception e) {
                        limit = 0.6;
                    }
                } else {
                    try {
                        limit = Integer.parseInt(str); // km/h
                    } catch (Exception e) {
                        limit = 45;
                    }
                    limit = limit / 3.6 / 20;
                }
            }
            int power = 10;
            if (limit >= 0.8)
                power += 5;
            if (limit >= 1.0)
                power += 5;

            Util.say(player, "[NSR] P: " + power + ", V: " + String.format("%1.2f", limit));

            facing = player.getHorizontalFacing();
            pos = pos.up();

            TileEntityActuator reception, triBoxP, triBoxN;
            TileEntityTransceiver sniffer; TileEntityReceiver rfid, light;
            Tuple<TileEntityTransceiver, TileEntityTransceiver> blocking;

            rfid = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facing, 7), player, power, 0, limit, true, true, true);
            sniffer = DeployHelper.INSTANCE.placeSniffer(world, pos.offset(facing, 6), player, true);
                DeployHelper.INSTANCE.connect(sniffer, rfid);
            light = DeployHelper.INSTANCE.placeSignalLight(world, pos.offset(facing, 4), player);
            triBoxP = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facing), player, false);
                DeployHelper.INSTANCE.connect(sniffer, triBoxP);
            reception = DeployHelper.INSTANCE.placeReception(world, pos, player, true);
                DeployHelper.INSTANCE.connect(reception, light);
            triBoxN = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facing.getOpposite()), player, true);
                DeployHelper.INSTANCE.connect(triBoxP, reception);
                DeployHelper.INSTANCE.connect(triBoxN, reception);
            blocking = DeployHelper.INSTANCE.placeBlocking(world, pos.offset(facing.getOpposite(), 3), pos.offset(facing.getOpposite(), 7), player, true);
            rfid = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facing.getOpposite(), 6), player, 0, 9, 0, false, false, true);
                DeployHelper.INSTANCE.connect(blocking.getSecond(), rfid);
                DeployHelper.INSTANCE.connect(blocking.getFirst(), triBoxN);

            Util.say(player, "info.PierBuilder.finish");
        }

        return world.isRemote ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
    }

}
