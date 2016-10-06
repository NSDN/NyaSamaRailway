package club.nsdn.nyasamarailway.Entity;

import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.8.26.
 */
public class NSPCT3 extends MinecartBase {

    public NSPCT3(World world) { super(world); }

    public NSPCT3(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 5.0F;
    }

}
