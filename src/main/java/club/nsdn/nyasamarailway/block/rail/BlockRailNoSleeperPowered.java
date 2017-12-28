package club.nsdn.nyasamarailway.block.rail;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2016.5.6.
 */

public class BlockRailNoSleeperPowered extends BlockRailPoweredBase {

    public BlockRailNoSleeperPowered() {
        super("BlockRailNoSleeperPowered");
        setTextureName("rail_ns_powered");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 5.0F;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        float maxV = getRailMaxSpeed(world, cart, x, y, z);
        if (world.getBlockMetadata(x, y, z) >= 8) {
            if (Math.abs(cart.motionX) < maxV && Math.abs(cart.motionZ) < maxV) {
                cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 2.0, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 2.0, 0.02);
            }
        } else {
            cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.1, 0.02);
            cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.1, 0.02);
        }
    }

}
