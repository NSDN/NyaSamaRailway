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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemPierBuilder extends ToolBase {

    public static final int TRAVERSAL_MAX = 128;
    public static int traversalCounter;

    public IBlockState endBlockState;

    public final Vec3i VECS[];

    public ItemPierBuilder() {
        super(ToolMaterial.STONE);
        setUnlocalizedName("ItemPierBuilder");
        setRegistryName(NyaSamaRailway.MODID, "pier_builder");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);

        endBlockState = null;

        LinkedList<Vec3i> list = new LinkedList<>();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                for (int k = -1; k <= 1; k++)
                {
                    if (i == 0 && j == 0 && k == 0)
                        continue;
                    list.add(new Vec3i(i, j, k));
                }
        VECS = list.toArray(new Vec3i[]{});
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public void TraversalBlocks(World world, IBlockState state, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;

        if (world.getBlockState(pos.down()).getBlock() == state.getBlock())
            return;

        traversalCounter += 1;
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
     * NOTE: pos is Base Block's Pos, traversal by base
     * */
    public void TraversalRails(World world, Block rail, IBlockState base, IBlockState edge, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;

        if ((world.getBlockState(pos.up()).getBlock() == rail) && traversalCounter > 1)
            return;

        traversalCounter += 1;
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

    public Tuple<BlockPos, EnumFacing> findNextRail(World world, Tuple<BlockPos, EnumFacing> now) {
        if (now == null) return null;

        IBlockState state = world.getBlockState(now.getFirst());
        Block block = state.getBlock();
        if (!(block instanceof BlockRailBase))
            return null;
        BlockRailBase rail = (BlockRailBase) block;
        BlockRailBase.EnumRailDirection railDir = rail.getRailDirection(world, now.getFirst(), state, null);
        switch (railDir) {
            case NORTH_SOUTH:
                switch (now.getSecond()) {
                    case NORTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().north(), now.getSecond());
                    case SOUTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().south(), now.getSecond());
                    default:
                        return null;
                }
            case EAST_WEST:
                switch (now.getSecond()) {
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().east(), now.getSecond());
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().west(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_NORTH:
                switch (now.getSecond()) {
                    case NORTH:
                        return new Tuple<>(now.getFirst().up().north(), now.getSecond());
                    case SOUTH:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_SOUTH:
                switch (now.getSecond()) {
                    case NORTH:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                    case SOUTH:
                        return new Tuple<>(now.getFirst().up().south(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_EAST:
                switch (now.getSecond()) {
                    case EAST:
                        return new Tuple<>(now.getFirst().up().east(), now.getSecond());
                    case WEST:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_WEST:
                switch (now.getSecond()) {
                    case EAST:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                    case WEST:
                        return new Tuple<>(now.getFirst().up().west(), now.getSecond());
                    default:
                        return null;
                }
            case NORTH_EAST:
                switch (now.getSecond()) {
                    case NORTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().north(), EnumFacing.NORTH);
                    case SOUTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().east(), EnumFacing.EAST);
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().east(), EnumFacing.EAST);
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().north(), EnumFacing.NORTH);
                    default:
                        return null;
                }
            case NORTH_WEST:
                switch (now.getSecond()) {
                    case NORTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().north(), EnumFacing.NORTH);
                    case SOUTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().west(), EnumFacing.WEST);
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().north(), EnumFacing.NORTH);
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().west(), EnumFacing.WEST);
                    default:
                        return null;
                }
            case SOUTH_EAST:
                switch (now.getSecond()) {
                    case NORTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().east(), EnumFacing.EAST);
                    case SOUTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().south(), EnumFacing.SOUTH);
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().east(), EnumFacing.EAST);
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().south(), EnumFacing.SOUTH);
                    default:
                        return null;
                }
            case SOUTH_WEST:
                switch (now.getSecond()) {
                    case NORTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().west(), EnumFacing.WEST);
                    case SOUTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().south(), EnumFacing.SOUTH);
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().south(), EnumFacing.SOUTH);
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().west(), EnumFacing.WEST);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public void placeEdgeEx(World world, IBlockState base, IBlockState edge, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        IBlockState stateDown = world.getBlockState(pos.down());
        if (state != base && !BlockRailBase.isRailBlock(state) && stateDown != base && stateDown != edge)
            world.setBlockState(pos, edge);
    }

    /**
     * NOTE: Traversal by rail
     * */
    public void TraversalRailsEx(World world, BlockRailBase rail, IBlockState base, IBlockState edge, BlockPos pos, EnumFacing startFacing) {
        Tuple<BlockPos, EnumFacing> next;
        BlockRailBase.EnumRailDirection railDir;
        IBlockState state;

        next = new Tuple<>(pos, startFacing);
        for (traversalCounter = 0; traversalCounter < TRAVERSAL_MAX; traversalCounter++) {
            next = findNextRail(world, next);
            if (next == null)
                break;
            pos = next.getFirst();

            state = world.getBlockState(pos);
            if (!BlockRailBase.isRailBlock(state))
                break;

            railDir = rail.getRailDirection(world, pos, state, null);

            pos = pos.down();
            world.setBlockState(pos, base);
            if (railDir.isAscending()) {
                switch (railDir) {
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                        world.setBlockState(pos.west(), base);
                        world.setBlockState(pos.east(), base);
                        break;
                    case ASCENDING_WEST:
                    case ASCENDING_EAST:
                        world.setBlockState(pos.north(), base);
                        world.setBlockState(pos.south(), base);
                        break;
                }
            } else {
                placeEdgeEx(world, base, edge, pos.east());
                placeEdgeEx(world, base, edge, pos.west());
                placeEdgeEx(world, base, edge, pos.north());
                placeEdgeEx(world, base, edge, pos.south());
                placeEdgeEx(world, base, edge, pos.east().north());
                placeEdgeEx(world, base, edge, pos.east().south());
                placeEdgeEx(world, base, edge, pos.west().north());
                placeEdgeEx(world, base, edge, pos.west().south());
            }
        }
    }

    public void TraversalFence(World world, IBlockState state, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;

        if (world.getBlockState(pos.up()).getBlock() == state.getBlock())
            return;

        traversalCounter += 1;
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

    public void RemoveBlocks6(World world, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;

        if (world.isAirBlock(pos))
            return;

        traversalCounter += 1;
        world.setBlockToAir(pos);

        RemoveBlocks6(world, pos.north());
        RemoveBlocks6(world, pos.south());
        RemoveBlocks6(world, pos.west());
        RemoveBlocks6(world, pos.east());
        RemoveBlocks6(world, pos.up());
        RemoveBlocks6(world, pos.down());
    }

    public void RemoveBlocks26(World world, BlockPos pos) {
        if (traversalCounter > TRAVERSAL_MAX) return;

        if (world.isAirBlock(pos))
            return;

        traversalCounter += 1;
        world.setBlockToAir(pos);

        for (Vec3i vec : VECS)
            RemoveBlocks26(world, pos.add(vec));
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == Blocks.AIR)
            return EnumActionResult.FAIL;

        if (player.getHeldItemOffhand().getItem() == this) {
            traversalCounter = 0;
            if (player.isSneaking())
                RemoveBlocks26(world, pos);
            else
                RemoveBlocks6(world, pos);

            return world.isRemote ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
        }

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
                    facing = player.getHorizontalFacing();
                    if (world.getBlockState(pos.down().offset(facing)).getBlock() != base &&
                            rail instanceof BlockRailBase
                    )
                        TraversalRailsEx(world, (BlockRailBase) rail, base, edge, pos, facing);
                    else
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
