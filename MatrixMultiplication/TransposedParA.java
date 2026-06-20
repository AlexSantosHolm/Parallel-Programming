public class TransposedParA implements Runnable{
    private double[][] aT, b, c;
    private int startRow, endRow;

    public TransposedParA(double[][] aT, double[][] b, double[][] c, int startRow, int endRow) {
        this.aT = aT;
        this.b = b;
        this.c = c;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run() {
        for (int i = startRow; i < endRow; ++i) {
            for (int j = 0; j < aT.length; ++j) {
                for (int k = 0; k < aT.length; ++k) {
                    c[i][j] += aT[k][i] * b[k][j];
                }
            }
        }
    }
}