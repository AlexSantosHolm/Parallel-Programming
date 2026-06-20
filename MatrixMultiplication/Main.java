import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Params: java Main <seed> [n]");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        int[] sizes;
        int n = 0;
        if (args.length == 2) {
            sizes = new int[] {Integer.parseInt(args[1])};
            n = Integer.parseInt(args[1]);
        } else {
            sizes = new int[] {100, 200, 500, 1000};
        }

        int numThreads = Runtime.getRuntime().availableProcessors();

        double[][] runtimesSeq = new double[4][7];
        double[][] runtimesSeqTransA = new double[4][7];
        double[][] runtimesSeqTransB = new double[4][7];
        double[][] runtimesPar = new double[4][7];
        double[][] runtimesParTransA = new double[4][7];
        double[][] runtimesParTransB = new double[4][7];


        for (int i = 0; i < sizes.length; ++i) {
            for (int j = 0; j < 7; ++j) {
                double[][] a = Oblig2Precode.generateMatrixA(seed, sizes[i]);
                double[][] b = Oblig2Precode.generateMatrixB(seed, sizes[i]);

                double startTime = System.nanoTime();
                double[][] resultSeq = Sequential.multiplyMatrixSeq(a, b);
                double runTime = (System.nanoTime() - startTime);
                runtimesSeq[i][j] = runTime / 1000000;
            }
        }


        for (int i = 0; i < sizes.length; ++i) {
            for (int j = 0; j < 7; ++j) {
                double[][] a = Oblig2Precode.generateMatrixA(seed, sizes[i]);
                double[][] b = Oblig2Precode.generateMatrixB(seed, sizes[i]);

                double startTime = System.nanoTime();
                double[][] resultSeqTransA = Sequential.transposedSeqA(a, b);
                double runTime = (System.nanoTime() - startTime);
                runtimesSeqTransA[i][j] = runTime / 1000000;
            }
        }


        for (int i = 0; i < sizes.length; ++i) {
            for (int j = 0; j < 7; ++j) {
                double[][] a = Oblig2Precode.generateMatrixA(seed, sizes[i]);
                double[][] b = Oblig2Precode.generateMatrixB(seed, sizes[i]);

                double startTime = System.nanoTime();
                double[][] resultSeqTransB = Sequential.transposedSeqB(a, b);
                double runTime = (System.nanoTime() - startTime);
                runtimesSeqTransB[i][j] = runTime / 1000000;
            }
        }


        for (int i = 0; i < sizes.length; ++i) {
            for (int j = 0; j < 7; ++j) {
                double[][] a = Oblig2Precode.generateMatrixA(seed, sizes[i]);
                double[][] b = Oblig2Precode.generateMatrixB(seed, sizes[i]);

                double startTime = System.nanoTime();
                double[][] resultPar = Parallel.multiplyMatrixParallel(a, b, numThreads);
                double runTime = (System.nanoTime() - startTime);
                runtimesPar[i][j] = runTime / 1000000;
            }
        }


        for (int i = 0; i < sizes.length; ++i) {
            for (int j = 0; j < 7; ++j) {
                double[][] a = Oblig2Precode.generateMatrixA(seed, sizes[i]);
                double[][] b = Oblig2Precode.generateMatrixB(seed, sizes[i]);

                double startTime = System.nanoTime();
                double[][] resultParTransA = Parallel.transposedParallelA(a, b, numThreads);
                double runTime = (System.nanoTime() - startTime);
                runtimesParTransA[i][j] = runTime / 1000000;
            }
        }



        for (int i = 0; i < sizes.length; ++i) {
            for (int j = 0; j < 7; ++j) {
                double[][] a = Oblig2Precode.generateMatrixA(seed, sizes[i]);
                double[][] b = Oblig2Precode.generateMatrixB(seed, sizes[i]);

                double startTime = System.nanoTime();
                double[][] resultParTransB = Parallel.transposedParallelB(a, b, numThreads);
                double runTime = (System.nanoTime() - startTime);
                runtimesParTransB[i][j] = runTime / 1000000;
            }
        }


        System.out.println("--------------------------------------------------");
        System.out.println("Median runtime of sequential matrix multiplication: ");
        for (int i = 0; i < sizes.length; ++i) {
            Arrays.sort(runtimesSeq[i]);
            double median = runtimesSeq[i][3];
            System.out.println("Size: " + sizes[i] + ", Runtime: " + median);
        }
        System.out.println("--------------------------------------------------");


        System.out.println("--------------------------------------------------");
        System.out.println(("Median runtime of sequential matrix multiplication with a transposed: "));
        for (int i = 0; i < sizes.length; ++i) {
            Arrays.sort(runtimesSeqTransA[i]);
            double median = runtimesSeqTransA[i][3];
            System.out.println("Size: " + sizes[i] + ", Runtime: " + median);
        }
        System.out.println("--------------------------------------------------");


        System.out.println("--------------------------------------------------");
        System.out.println("Median runtime of sequential matrix multiplication with b transposed: ");
        for (int i = 0; i < sizes.length; ++i) {
            Arrays.sort(runtimesSeqTransB[i]);
            double median = runtimesSeqTransB[i][3];
            System.out.println("Size: " + sizes[i] + ", Runtime: " + median);
        }
        System.out.println("--------------------------------------------------");


        System.out.println("--------------------------------------------------");
        System.out.println("Median runtime of parallel matrix multiplication: ");
        for (int i = 0; i < sizes.length; ++i) {
            Arrays.sort(runtimesPar[i]);
            double median = runtimesPar[i][3];
            System.out.println("Size: " + sizes[i] + ", Runtime: " + median);
        }
        System.out.println("--------------------------------------------------");


        System.out.println("--------------------------------------------------");
        System.out.println("Median Runtime of parallel matrix multiplication with a transposed a");
        for (int i = 0; i < sizes.length; ++i) {
            Arrays.sort(runtimesParTransA[i]);
            double median = runtimesParTransA[i][3];
            System.out.println("Size: " + sizes[i] + ", Runtime: " + median);
        }
        System.out.println("--------------------------------------------------");


        System.out.println("--------------------------------------------------");
        System.out.println("Median runtime of parallel matrix multiplication with b transposed");
        for (int i = 0; i < sizes.length; ++i) {
            Arrays.sort(runtimesParTransB[i]);
            double median = runtimesParTransB[i][3];
            System.out.println("Size: " + sizes[i] + ", Runtime: " + median);
        }
        System.out.println("--------------------------------------------------");

        double speedUpSeqToSeqTransposeA = 0.0;
        double speedUpSeqToSeqTransposeB = 0.0;
        double speedUpSeqToParallel = 0.0;
        double speedUpSeqToParallelTransposeA = 0.0;
        double speedUpSeqToParallelTransposeB = 0.0;


        for (int i = 0; i < sizes.length; ++i) {
            speedUpSeqToSeqTransposeA = runtimesSeq[i][3] / runtimesSeqTransA[i][3];
            speedUpSeqToSeqTransposeB = runtimesSeq[i][3] / runtimesSeqTransB[i][3];
            speedUpSeqToParallel = runtimesSeq[i][3] / runtimesPar[i][3];
            speedUpSeqToParallelTransposeA = runtimesSeq[i][3] / runtimesParTransA[i][3];
            speedUpSeqToParallelTransposeB = runtimesSeq[i][3] / runtimesParTransB[i][3];
            System.out.println("--------------------------------------------------");
            System.out.println("Speedups from sequential matrix multiplication for size: " + sizes[i]);
            System.out.println("Speedup Sequential / Sequentia Transpose A: " + speedUpSeqToSeqTransposeA);
            System.out.println("Speedup Sequential / Sequential Transpose B: " + speedUpSeqToSeqTransposeB) ;
            System.out.println("Speedup Sequential / Parallel: " + speedUpSeqToParallel);
            System.out.println("Speedup Sequential / Parallel Tranpose A: " + speedUpSeqToParallelTransposeA);
            System.out.println("Speedup Sequential / Parallel Transpose B: " + speedUpSeqToParallelTransposeB);
            System.out.println("--------------------------------------------------");
        }

        if (n != 0) {
            validateResults(seed, n, numThreads);
        }
        System.out.println("Number of threads: " + numThreads);
    }


    public static void validateResults(int seed, int n, int numThreads) {
        double[][] a = Oblig2Precode.generateMatrixA(seed, n);
        double[][] b = Oblig2Precode.generateMatrixB(seed, n);

        double[][] resultSeq = Sequential.multiplyMatrixSeq(a, b);
        double[][] resultSeqTransA = Sequential.transposedSeqA(a, b);
        double[][] resultSeqTransB = Sequential.transposedSeqB(a, b);
        double[][] resultPar = Parallel.multiplyMatrixParallel(a, b, numThreads);
        double[][] resultParTransA = Parallel.transposedParallelA(a, b, numThreads);
        double[][] resultParTransB = Parallel.transposedParallelB(a, b, numThreads);

        double tolerance = 1e-6;
        boolean allTrue = true;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (Math.abs(resultSeq[i][j] - resultSeqTransA[i][j]) > tolerance ||
                    Math.abs(resultSeq[i][j] - resultSeqTransB[i][j]) > tolerance ||
                    Math.abs(resultSeq[i][j] - resultPar[i][j]) > tolerance ||
                    Math.abs(resultSeq[i][j] - resultParTransA[i][j]) > tolerance ||
                    Math.abs(resultSeq[i][j] - resultParTransB[i][j]) > tolerance) {
                        allTrue = false;
                    }
            }
        }

        if (allTrue) {
            System.out.println("Validation passed! All results within tolerance.");
        } else {
            System.out.println("Validation not passed! Not all results within tolerance.");
        }
    }
}
