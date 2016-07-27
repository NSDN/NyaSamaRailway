package club.nsdn.nyasamarailway.Entity;

import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.26.
 */
public class NSPCT2 extends MinecartBase {

    public NSPCT2(World world) { super(world); }

    public NSPCT2(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 1.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.5;
    }

    @Override
    public boolean shouldRiderSit()
    {
        return false;
    }

}
