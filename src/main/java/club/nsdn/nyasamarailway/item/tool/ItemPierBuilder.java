package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.tileblock.functional.BlockPierTag;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.8.23.
 */
public class ItemPierBuilder extends ItemToolBase {

    public static final int TRAVERSAL_MAX = 128;
    public static int traversalCounter;

    public Block endBlock;

    public ItemPierBuilder() {
        super(ToolMaterial.STONE);
        setUnlocalizedName("ItemPierBuilder");
        setTexName("pier_builder");

        endBlock = null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    public void TraversalBlocks(World world, Block block, int x, int y, int z) {
        if (traversalCounter > TRAVERSAL_MAX) return;
        traversalCounter += 1;

        if (world.getBlock(x, y - 1, z) == block)
            return;

        world.setBlock(x, y - 1, z, block);

        if (world.getBlock(x + 1, y, z) == block) {
            TraversalBlocks(world, block, x + 1, y, z);
        }
        if (world.getBlock(x - 1, y, z) == block) {
            TraversalBlocks(world, block, x - 1, y, z);
        }
        if (world.getBlock(x, y, z + 1) == block) {
            TraversalBlocks(world, block, x, y, z + 1);
        }
        if (world.getBlock(x, y, z - 1) == block) {
            TraversalBlocks(world, block, x, y, z - 1);
        }
    }

    public boolean reachesEndBlock(World world, int x, int y, int z) {
        boolean flag = world.getBlock(x, y, z) == endBlock;

        Material material = world.getBlock(x, y, z).getMaterial();
        flag |= (
            material == Material.clay || material == Material.ground ||
            material == Material.iron || material == Material.rock
        );

        return flag;
    }

    public void placeEdge(World world, Block base, Block edge, int edgeMeta, int x, int y, int z) {
        if (world.getBlock(x, y, z) != base) {
            world.setBlock(x, y, z, edge);
            world.setBlockMetadataWithNotify(x, y, z, edgeMeta, 3);
        }
    }

    public Block getEdge(World world, int x, int y, int z) {
        if (
            world.getBlock(x + 1, y - 1, z) instanceof BlockSlab &&
            world.getBlock(x - 1, y - 1, z) instanceof BlockSlab
        ) return world.getBlock(x + 1, y - 1, z);
        if (
            world.getBlock(x, y - 1, z + 1) instanceof BlockSlab &&
            world.getBlock(x, y - 1, z - 1) instanceof BlockSlab
        ) return world.getBlock(x, y - 1, z + 1);

        return null;
    }

    public int getEdgeMeta(World world, int x, int y, int z) {
        if (
            world.getBlock(x + 1, y - 1, z) instanceof BlockSlab &&
            world.getBlock(x - 1, y - 1, z) instanceof BlockSlab
        ) return world.getBlockMetadata(x + 1, y - 1, z);
        if (
            world.getBlock(x, y - 1, z + 1) instanceof BlockSlab &&
            world.getBlock(x, y - 1, z - 1) instanceof BlockSlab
        ) return world.getBlockMetadata(x, y - 1, z + 1);

        return 0;
    }

    /**
     * NOTE: X, Y and Z are Base Block's Pos
     * */
    public void TraversalRails(World world, Block rail, Block base, Block edge, int edgeMeta, int x, int y, int z) {
        if (traversalCounter > TRAVERSAL_MAX) return;
        traversalCounter += 1;

        if ((world.getBlock(x, y + 1, z) == rail) && traversalCounter > 1)
            return;

        world.setBlock(x, y + 1, z, rail);
        placeEdge(world, base, edge, edgeMeta, x + 1, y, z);
        placeEdge(world, base, edge, edgeMeta, x - 1, y, z);
        placeEdge(world, base, edge, edgeMeta, x, y, z + 1);
        placeEdge(world, base, edge, edgeMeta, x, y, z - 1);
        placeEdge(world, base, edge, edgeMeta, x + 1, y, z + 1);
        placeEdge(world, base, edge, edgeMeta, x + 1, y, z - 1);
        placeEdge(world, base, edge, edgeMeta, x - 1, y, z + 1);
        placeEdge(world, base, edge, edgeMeta, x - 1, y, z - 1);

        if (world.getBlock(x + 1, y, z) == base) {
            TraversalRails(world, rail, base, edge, edgeMeta, x + 1, y, z);
        }
        if (world.getBlock(x - 1, y, z) == base) {
            TraversalRails(world, rail, base, edge, edgeMeta, x - 1, y, z);
        }
        if (world.getBlock(x, y, z + 1) == base) {
            TraversalRails(world, rail, base, edge, edgeMeta, x, y, z + 1);
        }
        if (world.getBlock(x, y, z - 1) == base) {
            TraversalRails(world, rail, base, edge, edgeMeta, x, y, z - 1);
        }
    }

    public boolean isRailBuilding(World world, int x, int y, int z) {
        boolean res = world.getBlock(x, y, z) instanceof BlockRail;
        res &= (
            (
                world.getBlock(x + 1, y - 1, z) instanceof BlockSlab &&
                world.getBlock(x - 1, y - 1, z) instanceof BlockSlab
            ) || (
                world.getBlock(x, y - 1, z + 1) instanceof BlockSlab &&
                world.getBlock(x, y - 1, z - 1) instanceof BlockSlab
            )
        );
        return res;
    }

    public void TraversalFence(World world, Block block, int meta, int x, int y, int z) {
        if (traversalCounter > TRAVERSAL_MAX) return;
        traversalCounter += 1;

        if (world.getBlock(x, y + 1, z) == block)
            return;

        world.setBlock(x, y + 1, z, block);
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);

        if (world.getBlock(x + 1, y, z) == block) {
            TraversalFence(world, block, meta, x + 1, y, z);
        }
        if (world.getBlock(x - 1, y, z) == block) {
            TraversalFence(world, block, meta, x - 1, y, z);
        }
        if (world.getBlock(x, y, z + 1) == block) {
            TraversalFence(world, block, meta, x, y, z + 1);
        }
        if (world.getBlock(x, y, z - 1) == block) {
            TraversalFence(world, block, meta, x, y, z - 1);
        }
    }

    public boolean checkFence(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) instanceof BlockFence ||
               world.getBlock(x, y, z) instanceof BlockPane;
    }

    public boolean isFenceBuilding(World world, int x, int y, int z) {
        boolean res = checkFence(world, x, y, z);

        res |= checkFence(world, x + 1, y, z);
        res |= checkFence(world, x - 1, y, z);
        res |= checkFence(world, x, y, z + 1);
        res |= checkFence(world, x, y, z - 1);

        return res;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block == null)
            return false;

        if (player.isSneaking()) {
            endBlock = block;
            if (player instanceof EntityPlayerMP)
                player.addChatComponentMessage(
                        new ChatComponentTranslation("info.PierBuilder.endBlock", endBlock.getLocalizedName())
                );
        } else {
            if (block instanceof BlockPierTag)  {
                if (endBlock == null)
                    return false;

                world.setBlock(x, y, z, Blocks.air);
                block = world.getBlock(x, y + 1, z);

                if (player instanceof EntityPlayerMP)
                    player.addChatComponentMessage(
                            new ChatComponentTranslation("info.PierBuilder.begin")
                    );

                // dy = -1 to use pier tag
                for (int dy = -1; !reachesEndBlock(world, x, y - dy - 1, z); dy++) {
                    traversalCounter = 0;
                    TraversalBlocks(world, block, x, y - dy, z);

                    if (traversalCounter > TRAVERSAL_MAX) {
                        if (player instanceof EntityPlayerMP)
                            player.addChatComponentMessage(
                                    new ChatComponentTranslation("info.PierBuilder.abort")
                            );
                        break;
                    }
                }

                if (player instanceof EntityPlayerMP)
                    player.addChatComponentMessage(
                            new ChatComponentTranslation("info.PierBuilder.finish")
                    );
            } else {
                if (isFenceBuilding(world, x, y, z)) {
                    Block fence = world.getBlock(x, y, z);
                    int meta = world.getBlockMetadata(x, y, z);

                    if (player instanceof EntityPlayerMP)
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.PierBuilder.fence")
                        );

                    traversalCounter = 0;
                    TraversalFence(world, fence, meta, x, y, z);
                    if (traversalCounter > TRAVERSAL_MAX) {
                        if (player instanceof EntityPlayerMP)
                            player.addChatComponentMessage(
                                    new ChatComponentTranslation("info.PierBuilder.abort")
                            );
                    } else {
                        if (player instanceof EntityPlayerMP)
                            player.addChatComponentMessage(
                                    new ChatComponentTranslation("info.PierBuilder.finish")
                            );
                    }
                } else if (isRailBuilding(world, x, y, z)) {
                    Block rail = world.getBlock(x, y, z);
                    Block base = world.getBlock(x, y - 1, z);
                    if (getEdge(world, x, y, z) == null)
                        return false;
                    Block edge = getEdge(world, x, y, z);
                    int edgeMeta = getEdgeMeta(world, x, y, z);

                    if (player instanceof EntityPlayerMP)
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.PierBuilder.rails")
                        );

                    traversalCounter = 0;
                    TraversalRails(world, rail, base, edge, edgeMeta, x, y - 1, z);
                    if (traversalCounter > TRAVERSAL_MAX) {
                        if (player instanceof EntityPlayerMP)
                            player.addChatComponentMessage(
                                    new ChatComponentTranslation("info.PierBuilder.abort")
                            );
                    } else {
                        if (player instanceof EntityPlayerMP)
                            player.addChatComponentMessage(
                                    new ChatComponentTranslation("info.PierBuilder.finish")
                            );
                    }
                } else return false;
            }
        }

        return !world.isRemote;
    }
}
