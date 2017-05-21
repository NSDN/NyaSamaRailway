package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.item.Item;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.5.21.
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
