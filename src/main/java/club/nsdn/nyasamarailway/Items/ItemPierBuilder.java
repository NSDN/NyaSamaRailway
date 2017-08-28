package club.nsdn.nyasamarailway.Items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
            if (endBlock == null)
                return false;

            if (player instanceof EntityPlayerMP)
                player.addChatComponentMessage(
                        new ChatComponentTranslation("info.PierBuilder.begin")
                );

            for (int dy = 0; !reachesEndBlock(world, x, y - dy - 1, z); dy++) {
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
        }

        return !world.isRemote;
    }
}
