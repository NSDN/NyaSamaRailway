package org.thewdj.physics;

/**
 * Created by drzzm32 on 2016.5.8.
 */
public class Dynamics {
    public static final double G = 9.8;

    public static double vel(double vx, double vz) {
        return Math.sqrt(vx * vx + vz * vz);
    }

    public static class LocoMotions {

        public static double calcVelocityUp(double vp, double u, double m, double P, double dt) {
            if (vp > 0) {
                    double f = u * m * G;
                    double amax = (1.0 - u) * G;
                    double a = (P - f * vp) / (vp * m);
                    if (a > amax) a = amax;
                    return vp + a * dt;
            } else {
                return 0.0;
            }
        }

        public static double calcVelocityUp(double F, double vp, double u, double m, double P, double dt) {
            if (vp > 0) {
                double f = u * m * G;
                double amax = (1.0 - u) * G;
                double a = (P + (F - f) * vp) / (vp * m);
                if (a > amax) a = amax;
                return vp + a * dt;
            } else {
                return 0.0;
            }
        }

        public static double calcVelocityDown(double vp, double u, double m, double B, double L, double R, double dt) {
            if (vp > 0) {
                double f = u * m * G;
                double amax = (1.0 + u) * G;
                double a = (B * B * L * L * vp / R + f) / m;
                if (a > amax) a = amax;
                if (a < u * G) a = u * G;
                return vp - a * dt;
            } else {
                return 0.0;
            }
        }

        public static double calcVelocityDown(double F, double vp, double u, double m, double B, double L, double R, double dt) {
            if (vp > 0) {
                double f = u * m * G;
                double amax = (1.0 + u) * G;
                double a = (B * B * L * L * vp / R + f - F) / m;
                if (a > amax) a = amax;
                if (a < u * G) a = u * G;
                return vp - a * dt;
            } else {
                return 0.0;
            }
        }

        public static double calcVelocityUpWithAir(double vp, double u, double m, double P, double dt) {
            if (vp > 0) {
                double k = 0.25;
                double p = 0.5;
                double f = p * u * m * G + (1 - p) * k * vp * vp;
                double a = (P - f * vp) / (vp * m);
                return vp + a * dt;
            } else {
                return 0.0;
            }
        }

        public static double calcVelocityDownWithAir(double vp, double u, double m, double B, double L, double R, double dt) {
            if (vp > 0) {
                double k = 0.25;
                double p = 0.5;
                double f = p * u * m * G + (1 - p) * k * vp * vp;
                double a = (B * B * L * L * vp / R + f) / m;
                return vp - a * dt;
            } else {
                return 0.0;
            }
        }

        public static double fMod(double f, double v) {
            double k = 1;
            double res = (1 - Math.exp(-k * v * v));
            res =  f * res;
            return res;
        }

        public static double ElectricLoco(double y, double P, double R, String type) {
            double m = 5000, g = 9.8;
            double k = 0.1, B = 1, L = 10;
            double v = y * 3.6;
            double ub = 0.31 + 3 / (30 + 10 * v);
            double ud = 0.06 + 46.6 / (260 + v);
            double fb = ub * m * g;
            double fd = ud * m * g;

            double res = 0, ft = 0;
            if (type.equals("up")) {
                ft = P / y;
                if (ft > fd) ft = fd;
                res = (ft - k * y * y) / m;
            } else if (type.equals("down")) {
                if (v < 120) B = B * 2;
                ft = (B * B * L * L * y) / R;
                if (v < 60) ft = fMod(fb, y);
                else if (ft > fd) ft = fd;
                res = -(ft + k * y * y) / m;
            }
            return res;
        }

        public static double calcVelocityWithEuler(double vp, double power, double brake, double maxV, double h) {
            vp = vp * 20;
            if (power < 0) power = 0; else if (power > 1) power = 1;
            if (brake < 0) brake = 0; else if (brake > 1) brake = 1;
            double P = 916.48 * Math.pow(maxV, 2.9305) * power;
            double R = (1 - brake) * 10 + 1;
            double k1 = ElectricLoco(vp, P, R, "up");
            double k2 = ElectricLoco(vp + h * k1, P, R, "up");
            double dv = h / 2 * (k1 + k2);
            if (brake > 0) {
                k1 = ElectricLoco(vp, P, R, "down");
                k2 = ElectricLoco(vp + h * k1, P, R, "down");
                dv = h / 2 * (k1 + k2);
            }
            double res = vp + dv;
            res = res / 20;
            return res;
        }

    }

}
