package club.nsdn.nyasamarailway.Entity;

import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.23.
 */
public class MinecartBase extends EntityMinecartEmpty {

    public MinecartBase(World world) { super(world); }

    public MinecartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public int getMinecartType() { return -1; }

    @Override
    public double getMountedYOffset() {
        return -0.1;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }
}
