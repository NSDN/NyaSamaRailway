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
public class ItemHelperThinLeft extends ToolBase {

    public ItemHelperThinLeft() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemHelperThinLeft");
        setRegistryName(NyaSamaRailway.MODID, "helper_thin_left");
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
                try {
                    limit = Double.parseDouble(str);
                } catch (Exception e) {
                    limit = 0.6;
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

            TileEntityActuator receptionA, triBoxPA, triBoxNA;
            TileEntityTransceiver snifferA; TileEntityReceiver rfidIA, rfidOA, lightA;
            Tuple<TileEntityTransceiver, TileEntityTransceiver> blockingA;

            TileEntityActuator receptionB, triBoxPB, triBoxNB;
            TileEntityTransceiver snifferB; TileEntityReceiver rfidIB, rfidOB, lightB;
            Tuple<TileEntityTransceiver, TileEntityTransceiver> blockingB;

            EnumFacing facneg = facing.getOpposite();

            rfidIA = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facing, 8), player, 0, 9, 0, false, false, true);
            rfidOA = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facing, 7), player, power, 0, limit, true, true, false);
            snifferA = DeployHelper.INSTANCE.placeSniffer(world, pos.offset(facing, 6), player, false);
            blockingA = DeployHelper.INSTANCE.placeBlocking(world, pos.offset(facing, 5), pos.offset(facing, 9), player, true);
            lightA = DeployHelper.INSTANCE.placeSignalLight(world, pos.offset(facing, 4), player);
            triBoxNA = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facing, 3), player, true);
            receptionA = DeployHelper.INSTANCE.placeReception(world, pos.offset(facing, 2), player, false, true);
            triBoxPA = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facing, 1), player, false);

            triBoxPB = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facneg, 1), player, false);
            receptionB = DeployHelper.INSTANCE.placeReception(world, pos.offset(facneg, 2), player, false, false);
            triBoxNB = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facneg, 3), player, true);
            lightB = DeployHelper.INSTANCE.placeSignalLight(world, pos.offset(facneg, 4), player, true);
            blockingB = DeployHelper.INSTANCE.placeBlocking(world, pos.offset(facneg, 5), pos.offset(facneg, 9), player, false);
            snifferB = DeployHelper.INSTANCE.placeSniffer(world, pos.offset(facneg, 6), player, true);
            rfidOB = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facneg, 7), player, power, 0, limit, true, true, true);
            rfidIB = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facneg, 8), player, 0, 9, 0, false, false, false);

            DeployHelper.INSTANCE.connect(receptionA, lightB);
            DeployHelper.INSTANCE.connect(triBoxPA, receptionA);
            DeployHelper.INSTANCE.connect(triBoxNA, receptionA);
            DeployHelper.INSTANCE.connect(blockingA.getFirst(), triBoxNA);
            DeployHelper.INSTANCE.connect(snifferB, triBoxPA);
            DeployHelper.INSTANCE.connect(snifferB, rfidOB);
            DeployHelper.INSTANCE.connect(blockingA.getSecond(), rfidIA);

            DeployHelper.INSTANCE.connect(receptionB, lightA);
            DeployHelper.INSTANCE.connect(triBoxPB, receptionB);
            DeployHelper.INSTANCE.connect(triBoxNB, receptionB);
            DeployHelper.INSTANCE.connect(blockingB.getFirst(), triBoxNB);
            DeployHelper.INSTANCE.connect(snifferA, triBoxPB);
            DeployHelper.INSTANCE.connect(snifferA, rfidOA);
            DeployHelper.INSTANCE.connect(blockingB.getSecond(), rfidIB);

            Util.say(player, "info.PierBuilder.finish");
        }

        return world.isRemote ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
    }

}
