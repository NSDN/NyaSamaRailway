package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.Blocks.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2016.6.7.
 */
public class Item74HC04 extends ItemToolBase {

    public Item74HC04() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("Item74HC04");
        setTexName("74hc04");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block == null)
            return false;

        if (block instanceof IRailDirectional) {
            if (((IRailDirectional) block).isForward()) {
                if (block instanceof BlockRailReception) world.setBlock(x, y, z, BlockLoader.blockRailReceptionAnti);
                if (block instanceof BlockRailProtectHead) world.setBlock(x, y, z, BlockLoader.blockRailProtectHeadAnti);
                if ((block instanceof BlockRailDirectional)) world.setBlock(x, y, z, BlockLoader.blockRailDirectionalAnti);
            } else {
                if (block instanceof BlockRailReceptionAnti) world.setBlock(x, y, z, BlockLoader.blockRailReception);
                if (block instanceof BlockRailProtectHeadAnti) world.setBlock(x, y, z, BlockLoader.blockRailProtectHead);
                if ((block instanceof BlockRailDirectionalAnti)) world.setBlock(x, y, z, BlockLoader.blockRailDirectional);
            }
            return !world.isRemote;
        } else if (block instanceof BlockRailDetectorBase) {
            int nowDelay = ((BlockRailDetectorBase) block).getDelaySecond();
            if (block instanceof BlockRailStoneSleeperDetector) {
                switch (nowDelay) {
                    case 0:
                        world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector5s);
                        break;
                    case 5:
                        world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector15s);
                        break;
                    case 15:
                        world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector30s);
                        break;
                    case 30:
                        world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector);
                        break;
                }
            } else {
                switch (nowDelay) {
                    case 0:
                        world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector5s);
                        break;
                    case 5:
                        world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector15s);
                        break;
                    case 15:
                        world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector30s);
                        break;
                    case 30:
                        world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector);
                        break;
                }
            }
            nowDelay = ((BlockRailDetectorBase) world.getBlock(x, y, z)).getDelaySecond();
            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.74HC04.delay", nowDelay));
            return !world.isRemote;
        }

        return false;
    }
}
