
public class MultiplyMatrixTask implements Runnable {
    private double[][] a, b, c;
    private int startRow, endRow;


    public MultiplyMatrixTask(double[][] a, double[][] b, double[][] c, int startRow, int endRow) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.startRow = startRow;
        this.endRow = endRow;
    }


    @Override
    public void run() {
        for (int i = startRow; i < endRow; ++i) {
            for (int j = 0; j < a.length; ++j) {
                for (int k = 0; k < a.length; ++k) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
    }
}


