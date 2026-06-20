import java.util.concurrent.*;

public class Parallel {

    public static double[][] multiplyMatrixParallel(double[][] a, double [][] b, int numThreads) {

        double[][] c = new double[a.length][a.length];

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int partitionSize = a.length / numThreads;
        for (int i = 0; i < numThreads; ++i) {
            int startRow = i * partitionSize;
            int endRow = (i == numThreads -1) ? a.length : startRow + partitionSize;

            executor.submit(new MultiplyMatrixTask(a, b, c, startRow, endRow));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return c;
    }

    public static double[][] transposeParallel(double[][] a, int numThreads) {
        double[][] t = new double[a.length][a.length];

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int partitionSize = a.length / numThreads;
        for (int i = 0; i < numThreads; ++i) {
            int startRow = i * partitionSize;
            int endRow = ( i == numThreads -1) ? a.length : startRow + partitionSize;

            executor.submit(new TransposeMatrixTask(a, t, startRow, endRow));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return t;
    }


    public static double[][] transposedParallelA(double[][] a, double[][] b, int numThreads) {

        double[][] c = new double[a.length][a.length];
        double[][] aT = transposeParallel(a, numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int partitionSize = a.length / numThreads;
        for (int i = 0; i < numThreads; ++i) {
            int startRow = i * partitionSize;
            int endRow = (i == numThreads - 1) ? a.length : startRow + partitionSize;

            executor.submit(new TransposedParA(aT, b, c, startRow, endRow));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return c;
    }

    public static double[][] transposedParallelB(double[][] a, double[][] b, int numThreads) {

        double[][] c = new double[a.length][a.length];
        double[][] bT = transposeParallel(b, numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int partitionSize = a.length / numThreads;
        for (int i = 0; i < numThreads; ++i) {
            int startRow = i * partitionSize;
            int endRow = (i == numThreads - 1) ? a.length : startRow + partitionSize;

            executor.submit(new TransposedParB(a, bT, c, startRow, endRow));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return c;
    }
}
