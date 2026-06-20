public class TransposeMatrixTask implements Runnable {
    private double[][] a, t;
    private int startRow, endRow;

    public TransposeMatrixTask(double[][] a, double[][] t, int startRow, int endRow) {
        this.a = a;
        this.t = t;
        this.startRow = startRow;
        this.endRow = endRow;
    }


    @Override
    public void run() {
        for (int i = startRow; i < endRow; ++i) {
            for (int j = 0; j < a.length; ++j) {
                t[i][j] += a[j][i];
            }
        }
    }
}