package club.nsdn.nyasamarailway.block.rail;

import club.nsdn.nyasamarailway.block.BlockLoader;
import net.minecraft.item.Item;

import java.util.Random;

/**
 * Created by drzzm32 on 2016.5.6.
 */

public class BlockRailStoneSleeperDetector extends BlockRailDetectorBase {

    public BlockRailStoneSleeperDetector() {
        super("BlockRailStoneSleeperDetector");
        setTextureName("rail_detector");
    }

    public BlockRailStoneSleeperDetector(int delay) {
        super("BlockRailStoneSleeperDetector", delay);
        setTextureName("rail_detector");
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(BlockLoader.blockRailStoneSleeperDetector);
    }

}
