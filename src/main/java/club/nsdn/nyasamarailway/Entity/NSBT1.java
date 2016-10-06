package club.nsdn.nyasamarailway.Entity;

import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.26.
 */
public class NSBT1 extends MinecartBase {

    public NSBT1(World world) { super(world); }

    public NSBT1(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

}
