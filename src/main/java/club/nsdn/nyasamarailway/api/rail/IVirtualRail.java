package club.nsdn.nyasamarailway.api.rail;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2020.2.15
 */
public interface IVirtualRail {
    float getTargetDirection(World world, BlockPos pos);

    static Vec3d getDirectionVec(float angle) {
        Vec3d vec = new Vec3d(0, 0, -1);
        vec = vec.rotateYaw(angle);
        return vec;
    }

}
