package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;
import net.minecraft.entity.item.EntityMinecart;

/**
 * Created by drzzm on 2016.10.6.
 */
public class BlockRailDirectional extends BlockRailBase implements IRailDirectional {

    public BlockRailDirectional() {
        super("BlockRailDirectional");
        setTextureName("rail_dir");
    }

    public boolean isForward() {
        return true;
    }

    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        double maxV = 0.2D;
        if (getRailDirection(world, x, y, z) == BlockRailBase.RailDirection.NS) {
            if (cart.motionZ >= 0.0D) cart.motionZ = -0.005D;
            if (cart.motionZ > -maxV)
                cart.motionZ = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
        } else {
            if (cart.motionX <= 0.0D) cart.motionX = 0.005D;
            if (cart.motionX < maxV) {
                cart.motionX = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1D, 1.0D, 0.1D, 0.02D);
            }
        }
    }

}
