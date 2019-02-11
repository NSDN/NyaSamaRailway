package club.nsdn.nyasamarailway.api.cart.nsc;

/**
 * Created by drzzm32 on 2019.2.10
 */
public interface IMonoRailCart {

    double DEFAULT_SHIFT = 0.0;
    double SLOPE_SHIFT = 0.25;

    default double updateShiftY(int meta, double shiftY) {
        if (SLOPE_SHIFT < DEFAULT_SHIFT) {
            if (meta >= 2 && meta <= 5) {
                if (shiftY > SLOPE_SHIFT) shiftY -= 0.05;
            } else if (shiftY < DEFAULT_SHIFT) shiftY += 0.05;
        } else {
            if (meta >= 2 && meta <= 5) {
                if (shiftY < SLOPE_SHIFT) shiftY += 0.05;
            } else if (shiftY > DEFAULT_SHIFT) shiftY -= 0.05;
        }
        return shiftY;
    }

    default double getDefaultShiftY(int meta) {
        if (meta >= 2 && meta <= 5)
            return SLOPE_SHIFT;
        return DEFAULT_SHIFT;
    }

    double getShiftY();
}
