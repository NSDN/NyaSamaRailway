package org.thewdj.physics;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by drzzm32 on 2016.6.10.
 */

public class Point3D {
    public int x;
    public int y;
    public int z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point3D) {
            Point3D p = (Point3D) obj;
            return p.x == x && p.y == y && p.z == z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", z=" + z;
    }
}
