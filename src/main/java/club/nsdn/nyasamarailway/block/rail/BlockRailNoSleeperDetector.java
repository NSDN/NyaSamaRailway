package club.nsdn.nyasamarailway.block.rail;

import club.nsdn.nyasamarailway.block.BlockLoader;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by drzzm32 on 2016.5.6.
 */

public class BlockRailNoSleeperDetector extends BlockRailDetectorBase {

    public BlockRailNoSleeperDetector() {
        super("BlockRailNoSleeperDetector");
        setTextureName("rail_ns_detector");
    }

    public BlockRailNoSleeperDetector(int delay) {
        super("BlockRailNoSleeperDetector", delay);
        setTextureName("rail_ns_detector");
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(BlockLoader.blockRailNoSleeperDetector);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 5.0F;
    }

}
