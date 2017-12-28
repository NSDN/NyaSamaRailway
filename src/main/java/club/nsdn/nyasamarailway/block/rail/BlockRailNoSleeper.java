package club.nsdn.nyasamarailway.block.rail;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.6.
 */

public class BlockRailNoSleeper extends BlockRailBase {

    public BlockRailNoSleeper() {
        super("BlockRailNoSleeper");
        setTextureName("rail_ns");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 5.0F;
    }

}
