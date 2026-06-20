
public class Sequential {


    public static double[][] multiplyMatrixSeq(double[][] a, double[][] b) {
        double[][] c = new double[a.length][a.length];

        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a.length; ++j) {
                for (int k = 0; k < a.length; ++k) {
                    c[i][j] += a[i][k] * b [k][j];
                }
            }
        }
        return c;
    }


    public static double[][] transpose(double[][] a) {
        double[][] t = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a.length; ++j) {
                t[i][j] = a[j][i];
            }
        }
        return t;
    }

    public static double[][] transposedSeqA(double[][] a, double[][] b) {
        double[][] c = new double[a.length][a.length];
        double[][] aT = transpose(a);

        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a.length; ++j) {
                for (int k = 0; k < a.length; ++k) {
                    c[i][j] += aT[k][i] * b[k][j];
                }
            }
        }
        return c;
    }


    public static double[][] transposedSeqB(double[][] a, double[][] b) {
        double[][] c = new double[a.length][a.length];
        double[][] bT = transpose(b);

        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a.length; ++j) {
                for (int k = 0; k < a.length; ++k) {
                    c[i][j] += a[i][k] * bT[j][k];
                }
            }
        }
        return c;
    }
}
