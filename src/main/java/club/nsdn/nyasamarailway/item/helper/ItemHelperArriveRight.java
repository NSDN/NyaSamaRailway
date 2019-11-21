package club.nsdn.nyasamarailway.item.helper;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.api.tileentity.*;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
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
public class ItemHelperArriveRight extends ToolBase {

    public ItemHelperArriveRight() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemHelperArriveRight");
        setRegistryName(NyaSamaRailway.MODID, "helper_arrive_right");
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

            TileEntityActuator reception, invBox, triBoxP, triBoxN;
            TileEntityTransceiver sniffer; TileEntityReceiver rfid, light;

            rfid = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facing, 4), player, 1, 0, 0.1, true, true, true);
            sniffer = DeployHelper.INSTANCE.placeSniffer(world, pos.offset(facing, 3), player, true);
                DeployHelper.INSTANCE.connect(sniffer, rfid);
            light = DeployHelper.INSTANCE.placeSignalLight(world, pos.offset(facing, 2), player);
            triBoxP = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facing), player, false);
                DeployHelper.INSTANCE.connect(sniffer, triBoxP);
            reception = DeployHelper.INSTANCE.placeReception(world, pos, player, true);
                DeployHelper.INSTANCE.connect(reception, light);
            triBoxN = DeployHelper.INSTANCE.placeTriSignalBox(world, pos.offset(facing.getOpposite()), player, true);
            invBox = DeployHelper.INSTANCE.placeSignalBox(world, pos.offset(facing.getOpposite(), 2), player, true);
                DeployHelper.INSTANCE.connect(invBox, reception);
                DeployHelper.INSTANCE.connect(triBoxP, reception);
                DeployHelper.INSTANCE.connect(triBoxN, reception);
            rfid = DeployHelper.INSTANCE.placeRFID(world, pos.offset(facing.getOpposite(), 3), player, 0, 9, 0, false, false, true);
            sniffer = DeployHelper.INSTANCE.placeSniffer(world, pos.offset(facing.getOpposite(), 4), player, true);
                DeployHelper.INSTANCE.connect(sniffer, rfid);
                DeployHelper.INSTANCE.connect(sniffer, triBoxN);

            Util.say(player, "info.PierBuilder.finish");
        }

        return world.isRemote ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
    }

}
