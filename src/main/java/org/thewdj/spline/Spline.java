package org.thewdj.spline;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by drzzm32 on 2019.3.9.
 */
public class Spline {

    public enum bd_type {
        first_deriv,
        second_deriv
    }

    private ArrayList<Double> m_x,m_y;
    private ArrayList<Double> m_a,m_b,m_c;
    private double m_b0, m_c0;

    private bd_type m_left, m_right;
    private double m_left_value, m_right_value;
    private boolean m_force_linear_extrapolation;

    public Spline() {
        m_left = bd_type.second_deriv;
        m_right = bd_type.second_deriv;
        m_left_value = 0.0;
        m_right_value = 0.0;
        m_force_linear_extrapolation = false;

        m_x = new ArrayList<>(); m_y = new ArrayList<>();
        m_a = new ArrayList<>(); m_b = new ArrayList<>(); m_c = new ArrayList<>();
    }

    public void toNBT(@Nonnull NBTTagCompound tagCompound, String prefix) {
        tagCompound.setDouble(prefix + "parB0", m_b0);
        tagCompound.setDouble(prefix + "parC0", m_c0);
        for (int i = 0; i < m_x.size(); i++)
            tagCompound.setDouble(prefix + "matX_" + i, m_x.get(i));
        for (int i = 0; i < m_y.size(); i++)
            tagCompound.setDouble(prefix + "matY_" + i, m_y.get(i));
        for (int i = 0; i < m_a.size(); i++)
            tagCompound.setDouble(prefix + "matA_" + i, m_a.get(i));
        for (int i = 0; i < m_b.size(); i++)
            tagCompound.setDouble(prefix + "matB_" + i, m_b.get(i));
        for (int i = 0; i < m_c.size(); i++)
            tagCompound.setDouble(prefix + "matC_" + i, m_c.get(i));
    }

    public void fromNBT(@Nonnull NBTTagCompound tagCompound, String prefix) {
        m_b0 = tagCompound.getDouble(prefix + "parB0");
        m_c0 = tagCompound.getDouble(prefix + "parC0");
        for (int i = 0; tagCompound.hasKey(prefix + "matX_" + i); i++)
            m_x.add(i, tagCompound.getDouble(prefix + "matX_" + i));
        for (int i = 0; tagCompound.hasKey(prefix + "matY_" + i); i++)
            m_y.add(i, tagCompound.getDouble(prefix + "matY_" + i));
        for (int i = 0; tagCompound.hasKey(prefix + "matA_" + i); i++)
            m_a.add(i, tagCompound.getDouble(prefix + "matA_" + i));
        for (int i = 0; tagCompound.hasKey(prefix + "matB_" + i); i++)
            m_b.add(i, tagCompound.getDouble(prefix + "matB_" + i));
        for (int i = 0; tagCompound.hasKey(prefix + "matC_" + i); i++)
            m_c.add(i, tagCompound.getDouble(prefix + "matC_" + i));
    }

    public void set_points(ArrayList<Double> x, ArrayList<Double> y) {
        set_points(x, y, true);
    }

    private static class Point {

        public double x, y;

        private Point(double x, double y) {
            this.x = x; this.y = y;
        }

        public static Point get(double x, double y) {
            return new Point(x, y);
        }

    }

    private void sort_points(ArrayList<Double> x, ArrayList<Double> y) {
        ArrayList<Point> points = new ArrayList<>(x.size());
        for (int i = 0; i < x.size(); i++)
            points.add(i, Point.get(x.get(i), y.get(i)));
        points.sort(Comparator.comparingDouble((p) -> p.x));
        for (int i = 0; i < points.size(); i++) {
            x.set(i, points.get(i).x);
            y.set(i, points.get(i).y);
        }
    }

    public void set_points(ArrayList<Double> x, ArrayList<Double> y, boolean cubic_spline) {
        assert(x.size() == y.size());
        assert(x.size() > 2);
        m_x = x;
        m_y = y;
        int n = x.size();

        sort_points(x, y);

        if (cubic_spline) {
            BandMatrix A = new BandMatrix(n, 1, 1);
            ArrayList<Double> rhs = new ArrayList<>(n);
            for (int i = 0; i < n; i++)
                rhs.add(0.0);

            for (int i = 1; i < n - 1; i++) {
                A.set(i,i - 1, 1.0 / 3.0 *(x.get(i)-x.get(i - 1)));
                A.set(i, i, 2.0 / 3.0 * (x.get(i + 1) - x.get(i - 1)));
                A.set(i, i + 1, 1.0 / 3.0 * (x.get(i + 1) - x.get(i)));
                rhs.set(i, (y.get(i + 1) - y.get(i)) / (x.get(i + 1) - x.get(i)) - (y.get(i) - y.get(i-1)) / (x.get(i) - x.get(i-1)));
            }

            if(m_left == bd_type.second_deriv) {
                A.set(0, 0, 2.0);
                A.set(0, 1, 0.0);
                rhs.set(0, m_left_value);
            } else if(m_left == bd_type.first_deriv) {
                A.set(0, 0, 2.0 * (x.get(1) - x.get(0)));
                A.set(0, 1, 1.0 * (x.get(1) - x.get(0)));
                rhs.set(0, 3.0 * ((y.get(1) - y.get(0)) / (x.get(1) - x.get(0)) - m_left_value));
            } else {
                assert(false);
            }
            if(m_right == bd_type.second_deriv) {
                A.set(n - 1, n - 1, 2.0);
                A.set(n - 1, n - 2, 0.0);
                rhs.set(n - 1, m_right_value);
            } else if(m_right == bd_type.first_deriv) {
                A.set(n - 1, n - 1, 2.0 * (x.get(n - 1) - x.get(n - 2)));
                A.set(n - 1, n - 2, 1.0 * (x.get(n - 1) - x.get(n - 2)));
                rhs.set(n - 1, 3.0 * (m_right_value - (y.get(n - 1) - y.get(n - 2)) / (x.get(n - 1) - x.get(n - 2))));
            } else {
                assert(false);
            }

            m_b = A.lu_solve(rhs);

            m_a = new ArrayList<>(n); for (int i = 0; i < n; i++) m_a.add(0.0);
            m_c = new ArrayList<>(n); for (int i = 0; i < n; i++) m_c.add(0.0);
            for (int i = 0; i < n - 1; i++) {
                m_a.set(i, 1.0 / 3.0 * (m_b.get(i + 1) - m_b.get(i)) / (x.get(i + 1) - x.get(i)));
                m_c.set(i,
                        (y.get(i + 1) - y.get(i)) / (x.get(i + 1) - x.get(i))
                        -
                        1.0 / 3.0 * (2.0 * m_b.get(i) + m_b.get(i + 1)) * (x.get(i + 1) - x.get(i))
                );
            }
        } else {
            m_a = new ArrayList<>(n); for (int i = 0; i < n; i++) m_a.add(0.0);
            m_b = new ArrayList<>(n); for (int i = 0; i < n; i++) m_b.add(0.0);
            m_c = new ArrayList<>(n); for (int i = 0; i < n; i++) m_c.add(0.0);
            for(int i = 0; i < n - 1; i++) {
                m_a.set(i, 0.0);
                m_b.set(i, 0.0);
                m_c.set(i, (m_y.get(i + 1) - m_y.get(i)) / (m_x.get(i + 1) - m_x.get(i)));
            }
        }

        m_b0 = !m_force_linear_extrapolation ? m_b.get(0) : 0.0;
        m_c0 = m_c.get(0);

        double h = x.get(n - 1) - x.get(n - 2);
        m_a.set(n - 1, 0.0);
        m_c.set(n - 1, 3.0 * m_a.get(n - 2) * h * h + 2.0 * m_b.get(n - 2) * h + m_c.get(n - 2));
        if(m_force_linear_extrapolation)
            m_b.set(n - 1, 0.0);
    }

    public double get(double x) {
        int n = m_x.size();

        // find the closest point m_x[idx] < x, idx=0 even if x<m_x[0]
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (m_x.get(i) >= x) {
                idx = i - 1;
                break;
            }
        }
        if (idx < 0) idx = 0;

        double h = x - m_x.get(idx);
        double interpol;
        if (x < m_x.get(0)) {
            interpol = (m_b0 * h + m_c0) * h + m_y.get(0);
        } else if (x > m_x.get(n - 1)) {
            interpol = (m_b.get(n - 1) * h + m_c.get(n - 1)) * h + m_y.get(n - 1);
        } else {
            interpol = ((m_a.get(idx) * h + m_b.get(idx)) * h + m_c.get(idx)) * h + m_y.get(idx);
        }
        return interpol;
    }

    /*public void set_boundary(bd_type left, double left_value, bd_type right, double right_value) {
        set_boundary(left, left_value, right, right_value, false);
    }

    public void set_boundary(bd_type left, double left_value, bd_type right, double right_value, boolean force_linear_extrapolation) {
        assert(m_x.size() == 0);

        m_left = left;
        m_right = right;
        m_left_value = left_value;
        m_right_value = right_value;
        m_force_linear_extrapolation = force_linear_extrapolation;
    }*/

    /*public double deriv(int order, double x) {
        assert(order > 0);

        int n = m_x.size();
        // find the closest point m_x[idx] < x, idx=0 even if x<m_x[0]
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (m_x.get(i) >= x) {
                idx = i - 1;
                break;
            }
        }

        double h = x - m_x.get(idx);
        double interpol;
        if (x < m_x.get(0)) {
            switch (order) {
                case 1:
                    interpol = 2.0 * m_b0 * h + m_c0;
                    break;
                case 2:
                    interpol = 2.0 * m_b0 * h;
                    break;
                default:
                    interpol = 0.0;
                    break;
            }
        } else if (x > m_x.get(n - 1)) {
            switch (order) {
                case 1:
                    interpol = 2.0 * m_b.get(n - 1) * h + m_c.get(n - 1);
                    break;
                case 2:
                    interpol = 2.0 * m_b.get(n - 1);
                    break;
                default:
                    interpol = 0.0;
                    break;
            }
        } else {
            switch (order) {
                case 1:
                    interpol = (3.0 * m_a.get(idx) * h + 2.0 * m_b.get(idx)) * h + m_c.get(idx);
                    break;
                case 2:
                    interpol = 6.0 * m_a.get(idx) * h + 2.0 * m_b.get(idx);
                    break;
                case 3:
                    interpol = 6.0 * m_a.get(idx);
                    break;
                default:
                    interpol = 0.0;
                    break;
            }
        }
        return interpol;
    }*/

}
