package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockRailDirectionalAnti extends BlockRailPoweredBase implements IRailDirectional {

    public BlockRailDirectionalAnti() {
        super("BlockRailDirectionalAnti");
        setTextureName("rail_dir_anti");
    }

    public boolean isForward() {
        return false;
    }

    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        double maxV = 0.4D;
        if (getRailDirection(world, x, y, z) == RailDirection.NS) {
            if (cart.motionZ <= 0.0D) cart.motionZ = 0.005D;
            if (cart.motionZ < maxV) {
                cart.motionZ = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
            }
        } else {
            if (cart.motionX >= 0.0D) cart.motionX = -0.005D;
            if (cart.motionX > -maxV) {
                cart.motionX = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1D, 1.0D, 0.1D, 0.02D);
            }
        }
    }

}
