public class TransposedParB implements Runnable {
    private double[][] a, bT, c;
    private int startRow, endRow;

    public TransposedParB(double[][] a, double[][] bT, double[][] c, int startRow, int endRow) {
        this.a = a;
        this.bT = bT;
        this.c = c;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run(){
        for (int i = startRow; i < endRow; ++i) {
            for (int j = 0; j < a.length; ++j) {
                for (int k = 0; k < a.length; ++k) {
                    c[i][j] += a[i][k] * bT[j][k];
                }
            }
        }
    }
}