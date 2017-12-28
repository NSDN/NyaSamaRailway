package club.nsdn.nyasamarailway.block.rail.special;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.6.17.
 */
public class BlockRailNoSleeperBlocking extends BlockRailBlocking {

    public BlockRailNoSleeperBlocking() {
        super("BlockRailNoSleeperBlocking", "rail_ns_blocking");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 5.0F;
    }

}
