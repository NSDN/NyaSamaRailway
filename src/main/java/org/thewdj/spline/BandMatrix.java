package org.thewdj.spline;

import java.util.ArrayList;

/**
 * Created by drzzm32 on 2019.3.9.
 */
public class BandMatrix {

    private ArrayList<ArrayList<Double>> m_upper;
    private ArrayList<ArrayList<Double>> m_lower;

    public BandMatrix() { }

    public BandMatrix(int dim, int n_u, int n_l) {
        resize(dim, n_u, n_l);
    }

    public void resize(int dim, int n_u, int n_l) {
        assert(dim > 0);
        assert(n_u >= 0);
        assert(n_l >= 0);

        m_upper = new ArrayList<>(n_u + 1);
        m_lower = new ArrayList<>(n_l + 1);
        for (int i = 0; i < n_u + 1; i++) {
            m_upper.add(i, new ArrayList<>(dim));
            for (int j = 0 ; j < dim; j++)
                m_upper.get(i).add(j, 0.0);
        }
        for (int i = 0; i < n_l + 1; i++) {
            m_lower.add(i, new ArrayList<>(dim));
            for (int j = 0 ; j < dim; j++)
                m_lower.get(i).add(j, 0.0);
        }
    }

    public int dim() {
        if(m_upper.size() > 0) {
            return m_upper.get(0).size();
        } else {
            return 0;
        }
    }

    public int num_upper() { return m_upper.size() - 1; }

    public int num_lower() { return m_lower.size() - 1; }

    public void set(int i, int j, double val) {
        int k = j - i;
        assert((i >= 0) && (i < dim()) && (j >= 0) && (j < dim()));
        assert((-num_lower() <= k) && (k <= num_upper()));

        if (k >= 0) {
            m_upper.get(k).set(i, val);
        } else {
            m_lower.get(-k).set(i, val);
        }
    }

    public double get(int i, int j) {
        int k = j - i;
        assert((i >= 0) && (i < dim()) && (j >= 0) && (j < dim()));
        assert((-num_lower() <= k) && (k <= num_upper()));

        if (k >= 0) {
            return m_upper.get(k).get(i);
        } else {
            return m_lower.get(-k).get(i);
        }
    }

    public void saved_diag_set(int i, double val) {
        assert((i >= 0) && (i < dim()));

        m_lower.get(0).set(i, val);
    }

    public double saved_diag_get(int i) {
        assert((i >= 0) && (i < dim()));

        return m_lower.get(0).get(i);
    }

    public void lu_decompose() {
        int i_max, j_max;
        int j_min;
        double x;

        for (int i = 0; i < dim(); i++) {
            assert(get(i, i) != 0.0);

            saved_diag_set(i, 1.0 / get(i, i));
            j_min = Math.max(0, i - num_lower());
            j_max = Math.min(dim() - 1, i + num_upper());
            for (int j = j_min; j <= j_max; j++) {
                set(i, j, get(i, j) * saved_diag_get(i));
            }
            set(i, i, 1.0);
        }

        for (int k = 0; k < dim(); k++) {
            i_max = Math.min(dim() - 1, k + num_lower());
            for (int i = k + 1; i <= i_max; i++) {
                assert(get(k, k) != 0.0);

                x = -get(i, k) / get(k, k);
                set(i, k, -x);
                j_max = Math.min(dim() - 1, k + num_upper());
                for (int j = k + 1; j <= j_max; j++) {
                    set(i, j, get(i, j) + x * get(k, j));
                }
            }
        }
    }

    ArrayList<Double> r_solve(ArrayList<Double> b) {
        assert(dim() == b.size());

        ArrayList<Double> x = new ArrayList<>(dim());
        for (int i = 0; i < dim(); i++) x.add(0.0);
        int j_stop;
        double sum;
        for (int i = dim() - 1; i >= 0; i--) {
            sum = 0;
            j_stop = Math.min(dim() - 1, i + num_upper());
            for (int j = i + 1; j <= j_stop; j++)
                sum += get(i,j) * x.get(j);
            x.set(i, (b.get(i) - sum) / get(i, i));
        }
        return x;
    }

    ArrayList<Double> l_solve(ArrayList<Double> b) {
        assert(dim() == b.size());

        ArrayList<Double> x = new ArrayList<>(dim());
        for (int i = 0; i < dim(); i++) x.add(0.0);
        int j_start;
        double sum;
        for (int i = 0; i < dim(); i++) {
            sum = 0;
            j_start = Math.max(0, i - num_lower());
            for (int j = j_start; j < i; j++)
                sum += get(i,j) * x.get(j);
            x.set(i, (b.get(i) * saved_diag_get(i)) - sum);
        }
        return x;
    }

    ArrayList<Double> lu_solve(ArrayList<Double> b) { return lu_solve(b, false); }

    ArrayList<Double> lu_solve(ArrayList<Double> b, boolean is_lu_decomposed) {
        assert(dim() == b.size());

        ArrayList<Double> x, y;
        if(!is_lu_decomposed) {
            lu_decompose();
        }
        y = l_solve(b);
        x = r_solve(y);

        return x;
    }

}
