package org.thewdj.physics;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

/**
 * Created by drzzm32 on 2016.7.9.
 */

public class Vec3d extends Vec3 {

    public Vec3d(Vec3 vec) {
        super(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3d(double x, double y, double z) {
        super(x, y, z);
    }

    public static Vec3d fromEntityPos(Entity entity) {
        return new Vec3d(entity.posX, entity.posY, entity.posZ);
    }

    public static Vec3d fromEntityMotion(Entity entity) {
        return new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
    }

    public Vec3d addVector(Vec3d vec) {
        return new Vec3d(this.xCoord + vec.xCoord, this.yCoord + vec.yCoord, this.zCoord + vec.zCoord);
    }

    public Vec3d dotProduct(double value) {
        return new Vec3d(this.xCoord * value, this.yCoord * value, this.zCoord * value);
    }
}
