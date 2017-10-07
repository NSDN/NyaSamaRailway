package org.thewdj.physics;

/**
 * Created by drzzm32 on 2016.5.8.
 */
public class Dynamics {
    public static final double G = 9.8;

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
                double f = p * u * G + (1 - p) * k * vp * vp;
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
                double f = p * u * G + (1 - p) * k * vp * vp;
                double a = (B * B * L * L * vp / R + f) / m;
                return vp - a * dt;
            } else {
                return 0.0;
            }
        }

    }

}
