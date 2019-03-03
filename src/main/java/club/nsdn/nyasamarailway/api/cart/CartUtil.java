package club.nsdn.nyasamarailway.api.cart;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Created by drzzm32 on 2019.2.11
 */
public class CartUtil {

    public static void updatePassenger2(Entity cart, Entity passenger) {
        double x = cart.posX, z = cart.posZ;
        double y = cart.posY + cart.getMountedYOffset() + passenger.getYOffset();
        if (cart.isPassenger(passenger)) {
            double index = (double) cart.getPassengers().indexOf(passenger);
            double dist = 0.5;
            double vx, vz;
            vx = dist * Math.cos(index * Math.PI);
            vz = dist * Math.sin(index * Math.PI);
            Vec3d vec = new Vec3d(vx, 0.0D, vz);
            vec = vec.rotatePitch((float) ((cart.rotationPitch + 360) / 180 * Math.PI));
            vec = vec.rotateYaw((float) ((180 - cart.rotationYaw) / 180 * Math.PI));
            passenger.setPosition(x + vec.x, y + vec.y, z + vec.z);
        }
    }

    public static void updatePassenger2e(Entity cart, Entity passenger) {
        double x = cart.posX, z = cart.posZ;
        double y = cart.posY + cart.getMountedYOffset() + passenger.getYOffset();
        if (cart.isPassenger(passenger)) {
            double index = (double) cart.getPassengers().indexOf(passenger);
            double dist = 0.5;
            double vx, vz;
            vx = dist * Math.cos(index * Math.PI);
            vz = dist * Math.sin(index * Math.PI);
            if (cart.getPassengers().size() == 1)
                vx = vz = 0;
            Vec3d vec = new Vec3d(vx, 0.0D, vz);
            vec = vec.rotatePitch((float) ((cart.rotationPitch + 360) / 180 * Math.PI));
            vec = vec.rotateYaw((float) ((180 - cart.rotationYaw) / 180 * Math.PI + Math.PI / 2));
            passenger.setPosition(x + vec.x, y + vec.y, z + vec.z);
        }
    }

    public static void updatePassenger4(Entity cart, Entity passenger) {
        double x = cart.posX, z = cart.posZ;
        double y = cart.posY + cart.getMountedYOffset() + passenger.getYOffset();
        if (cart.isPassenger(passenger)) {
            int index = cart.getPassengers().indexOf(passenger);
            double distX = 0.75, distZ = 0.325;
            double vx, vz;
            vx = distX * (double) (((index / 2) == 1) ? 1 : -1);
            vz = distZ * (double) (((index % 3) == 0) ? 1 : -1);
            Vec3d vec = new Vec3d(vx, 0.0D, vz);
            vec = vec.rotatePitch((float) ((cart.rotationPitch + 360) / 180 * Math.PI));
            vec = vec.rotateYaw((float) ((180 - cart.rotationYaw) / 180 * Math.PI));
            passenger.setPosition(x + vec.x, y + vec.y, z + vec.z);
        }
    }

    public static void updatePassenger6(Entity cart, Entity passenger) {
        double x = cart.posX, z = cart.posZ;
        double y = cart.posY + cart.getMountedYOffset() + passenger.getYOffset();
        if (cart.isPassenger(passenger)) {
            int index = cart.getPassengers().indexOf(passenger);
            double distX = 1.0, distZ = 0.5;
            double vx, vz; int[] array = { -1, 0, 1, 1, 0, -1 };
            vx = distX * (double) array[index];
            vz = distZ * (double) (((index / 3) == 0) ? 1 : -1);
            Vec3d vec = new Vec3d(vx, 0.0D, vz);
            vec = vec.rotatePitch((float) ((cart.rotationPitch + 360) / 180 * Math.PI));
            vec = vec.rotateYaw((float) ((180 - cart.rotationYaw) / 180 * Math.PI));
            passenger.setPosition(x + vec.x, y + vec.y, z + vec.z);
        }
    }

    public static void updatePassenger12(Entity cart, Entity passenger) {
        double x = cart.posX, z = cart.posZ;
        double y = cart.posY + cart.getMountedYOffset() + passenger.getYOffset();
        if (cart.isPassenger(passenger)) {
            int index = cart.getPassengers().indexOf(passenger);
            double distX = 1.0, distZ = 0.5;
            double vx, vz;
            int[] array = {
                    -2, -1, 0, 1, 2,
                    2, 1, 0, -1, -2
            };
            vx = distX * (double) array[index];
            vz = distZ * (double) (((index / 6) == 0) ? 1 : -1);
            Vec3d vec = new Vec3d(vx, 0.0D, vz);
            vec = vec.rotatePitch((float) ((cart.rotationPitch + 360) / 180 * Math.PI));
            vec = vec.rotateYaw((float) ((180 - cart.rotationYaw) / 180 * Math.PI));
            passenger.setPosition(x + vec.x, y + vec.y, z + vec.z);
        }
    }

}
