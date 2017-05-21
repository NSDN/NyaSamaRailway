package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.world.World;
import net.minecraft.entity.item.EntityMinecart;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockRailSignalTransfer extends BlockRailPoweredBase {

    public BlockRailSignalTransfer() {
        super("BlockRailSignalTransfer");
        setTextureName("rail_signal_transfer");
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
    }

}
