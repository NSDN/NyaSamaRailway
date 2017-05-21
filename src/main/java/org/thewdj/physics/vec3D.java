package org.thewdj.physics;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class vec3D extends Vec3d {

    public vec3D(Vec3d vec) {
        super(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public vec3D(double x, double y, double z) {
        super(x, y, z);
    }

    public static vec3D fromEntityPos(Entity entity) {
        return new vec3D(entity.posX, entity.posY, entity.posZ);
    }

    public static vec3D fromEntityMotion(Entity entity) {
        return new vec3D(entity.motionX, entity.motionY, entity.motionZ);
    }

    public vec3D addVector(vec3D vec) {
        return new vec3D(this.xCoord + vec.xCoord, this.yCoord + vec.yCoord, this.zCoord + vec.zCoord);
    }

    public vec3D dotProduct(double value) {
        return new vec3D(this.xCoord * value, this.yCoord * value, this.zCoord * value);
    }
}
