package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.tileblock.func.PierTag;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
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
 * Created by drzzm32 on 2019.2.10
 */
public class ItemPierBuilder extends ToolBase {

    public static final int TRAVERSAL_MAX = 128;
    public static int traversalCounter;

    public IBlockState endBlockState;

    public ItemPierBuilder() {
        super(ToolMaterial.STONE);
        setUnlocalizedName("ItemPierBuilder");
        setRegistryName(NyaSamaRailway.MODID, "pier_builder");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);

        endBlockState = null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public void TraversalBlocks(World world, IBlockState state, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;
        traversalCounter += 1;

        if (world.getBlockState(pos.down()).getBlock() == state.getBlock())
            return;

        world.setBlockState(pos.down(), state);

        if (world.getBlockState(pos.east()).getBlock() == state.getBlock())
            TraversalBlocks(world, state, pos.east());

        if (world.getBlockState(pos.west()).getBlock() == state.getBlock())
            TraversalBlocks(world, state, pos.west());

        if (world.getBlockState(pos.north()).getBlock() == state.getBlock())
            TraversalBlocks(world, state, pos.north());

        if (world.getBlockState(pos.south()).getBlock() == state.getBlock())
            TraversalBlocks(world, state, pos.south());
    }

    public boolean reachesEndBlock(World world, BlockPos pos) {
        boolean flag = world.getBlockState(pos) == endBlockState;

        Material material = world.getBlockState(pos).getMaterial();
        flag |= (
            material == Material.CLAY || material == Material.GROUND ||
            material == Material.IRON || material == Material.ROCK
        );

        return flag;
    }

    public void placeEdge(World world, IBlockState base, IBlockState edge, BlockPos pos) {
        if (world.getBlockState(pos) != base)
            world.setBlockState(pos, edge);
    }

    public IBlockState getEdge(World world, BlockPos pos) {
        if (
            world.getBlockState(pos.down().east()).getBlock() instanceof BlockSlab &&
            world.getBlockState(pos.down().west()).getBlock() instanceof BlockSlab
        ) return world.getBlockState(pos.down().east());
        if (
            world.getBlockState(pos.down().north()).getBlock() instanceof BlockSlab &&
            world.getBlockState(pos.down().south()).getBlock() instanceof BlockSlab
        ) return world.getBlockState(pos.down().north());

        return null;
    }

    /**
     * NOTE: pos is Base Block's Pos
     * */
    public void TraversalRails(World world, Block rail, IBlockState base, IBlockState edge, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;
        traversalCounter += 1;

        if ((world.getBlockState(pos.up()).getBlock() == rail) && traversalCounter > 1)
            return;

        world.setBlockState(pos.up(), rail.getDefaultState());
        placeEdge(world, base, edge, pos.east());
        placeEdge(world, base, edge, pos.west());
        placeEdge(world, base, edge, pos.north());
        placeEdge(world, base, edge, pos.south());
        placeEdge(world, base, edge, pos.east().north());
        placeEdge(world, base, edge, pos.east().south());
        placeEdge(world, base, edge, pos.west().north());
        placeEdge(world, base, edge, pos.west().south());

        if (world.getBlockState(pos.east()) == base)
            TraversalRails(world, rail, base, edge, pos.east());

        if (world.getBlockState(pos.west()) == base) {
            TraversalRails(world, rail, base, edge, pos.west());
        }
        if (world.getBlockState(pos.north()) == base) {
            TraversalRails(world, rail, base, edge, pos.north());
        }
        if (world.getBlockState(pos.south()) == base) {
            TraversalRails(world, rail, base, edge, pos.south());
        }
    }

    public boolean isRailBuilding(World world, BlockPos pos) {
        boolean res = world.getBlockState(pos).getBlock() instanceof BlockRailBase;
        res &= (
            (
                world.getBlockState(pos.down().east()).getBlock() instanceof BlockSlab &&
                world.getBlockState(pos.down().west()).getBlock() instanceof BlockSlab
            ) || (
                world.getBlockState(pos.down().north()).getBlock() instanceof BlockSlab &&
                world.getBlockState(pos.down().south()).getBlock() instanceof BlockSlab
            )
        );
        return res;
    }

    public void TraversalFence(World world, IBlockState state, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;
        traversalCounter += 1;

        if (world.getBlockState(pos.up()).getBlock() == state.getBlock())
            return;

        world.setBlockState(pos.up(), state);

        if (world.getBlockState(pos.east()).getBlock() == state.getBlock())
            TraversalFence(world, state, pos.east());

        if (world.getBlockState(pos.west()).getBlock() == state.getBlock())
            TraversalFence(world, state, pos.west());

        if (world.getBlockState(pos.north()).getBlock() == state.getBlock())
            TraversalFence(world, state, pos.north());

        if (world.getBlockState(pos.south()).getBlock() == state.getBlock())
            TraversalFence(world, state, pos.south());
    }

    public boolean checkFence(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof BlockFence ||
               world.getBlockState(pos).getBlock() instanceof BlockPane;
    }

    public boolean isFenceBuilding(World world, BlockPos pos) {
        boolean res = checkFence(world, pos);

        res |= checkFence(world, pos.east());
        res |= checkFence(world, pos.west());
        res |= checkFence(world, pos.north());
        res |= checkFence(world, pos.south());

        return res;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == Blocks.AIR)
            return EnumActionResult.FAIL;

        if (player.isSneaking()) {
            endBlockState = state;
            Util.say(player, "info.PierBuilder.endBlock", endBlockState.getBlock().getLocalizedName());
        } else {
            if (state.getBlock() instanceof PierTag)  {
                if (endBlockState == null)
                    return EnumActionResult.FAIL;

                world.setBlockToAir(pos);
                state = world.getBlockState(pos.up());

                Util.say(player, "info.PierBuilder.begin");

                // dy = -1 to use pier tag
                for (int dy = -1; !reachesEndBlock(world, pos.down(dy + 1)); dy++) {
                    traversalCounter = 0;
                    TraversalBlocks(world, state, pos.down(dy));

                    if (traversalCounter > TRAVERSAL_MAX) {
                        Util.say(player, "info.PierBuilder.abort");
                        break;
                    }
                }

                Util.say(player, "info.PierBuilder.finish");
            } else {
                if (isFenceBuilding(world, pos)) {
                    IBlockState fence = world.getBlockState(pos);

                    Util.say(player, "info.PierBuilder.fence");

                    traversalCounter = 0;
                    TraversalFence(world, fence, pos);
                    if (traversalCounter > TRAVERSAL_MAX) {
                        Util.say(player, "info.PierBuilder.abort");
                    } else {
                        Util.say(player, "info.PierBuilder.finish");
                    }
                } else if (isRailBuilding(world, pos)) {
                    Block rail = world.getBlockState(pos).getBlock();
                    IBlockState base = world.getBlockState(pos.down());
                    if (getEdge(world, pos) == null)
                        return EnumActionResult.FAIL;
                    IBlockState edge = getEdge(world, pos);

                    Util.say(player, "info.PierBuilder.rails");

                    traversalCounter = 0;
                    TraversalRails(world, rail, base, edge, pos.down());
                    if (traversalCounter > TRAVERSAL_MAX) {
                        Util.say(player, "info.PierBuilder.abort");
                    } else {
                        Util.say(player, "info.PierBuilder.finish");
                    }
                } else return EnumActionResult.FAIL;
            }
        }

        return world.isRemote ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
    }
}
