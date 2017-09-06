package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.8.26.
 */

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

public class BlockRailSpeedLimit extends BlockRailPoweredBase {

    public BlockRailSpeedLimit() {
        super("BlockRailSpeedLimit");
        setTextureName("rail_speed_limit");
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        float maxV = 0.2F;
        if (world.getBlockMetadata(x, y, z) >= 8) {
            if (Math.abs(cart.motionX) < maxV && Math.abs(cart.motionZ) < maxV) {
                cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 0.02);
            } else {
                cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.1, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.1, 0.02);
            }
        }
    }

}
