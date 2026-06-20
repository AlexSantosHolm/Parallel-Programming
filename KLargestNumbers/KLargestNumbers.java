import java.util.Arrays;
import java.util.Random;

public class KLargestNumbers {

    public static void insertSortDec(int[] a, int v, int h) {
        for (int k = v; k < h; ++k) {
            int t = a[k + 1];
            int i = k;
            while (i >= v && a[i] < t) {
                a[i + 1] = a[i];
                --i;
            }
            a[i + 1] = t;
        }
    }

    public static int[] findKLargest(int[] a, int k) {
        insertSortDec(a, 0, k - 1);

        for (int j = k; j < a.length; ++j) {
            if (a[j] > a[k - 1]) {
                a[k - 1] = a[j];

                int t = a[k - 1];
                int i = k - 2;
                while (i >= 0 && a[i] < t) {
                    a[i + 1] = a[i];
                    i--;
                }
                a[i + 1] = t;
            }
        }
        return Arrays.copyOfRange(a, 0, k);
    }

    public static int[] findKLargestRange(int[] a, int start, int end, int k) {
        int length = end - start;
        if (k > length) k = length;

        // Sort first k elements in descending order
        insertSortDecRange(a, start, start + k - 1);

        // Process the remaining elements
        for (int j = start + k; j < end; ++j) {
            if (a[j] > a[start + k - 1]) {
                a[start + k - 1] = a[j];

                int t = a[start + k - 1];
                int i = start + k - 2;
                while (i >= start && a[i] < t) {
                    a[i + 1] = a[i];
                    i--;
                }
                a[i + 1] = t;
            }
        }

        // Copy result to a new array
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = a[start + i];
        }
        return result;
    }

    public static void insertSortDecRange(int[] a, int v, int h) {
        for (int k = v; k < h; ++k) {
            int t = a[k + 1];
            int i = k;
            while (i >= v && a[i] < t) {
                a[i + 1] = a[i];
                --i;
            }
            a[i + 1] = t;
        }
    }

    static class WorkerThread extends Thread {
        private final int[] a;
        private final int start;
        private final int end;
        private final int k;
        private int[] result;

        public WorkerThread(int[] a, int start, int end, int k) {
            this.a = a;
            this.start = start;
            this.end = end;
            this.k = k;
        }

        @Override
        public void run() {
            result = findKLargestRange(a, start, end, k);
        }

        public int[] getResult() {
            return result;
        }
    }

    public static int[] findKLargestParallel(int[] a, int k, int numThreads) throws InterruptedException {
        int partitionSize = a.length / numThreads;
        WorkerThread[] threads = new WorkerThread[numThreads];

        // Create and start the threads
        for (int i = 0; i < numThreads; ++i) {
            int start = i * partitionSize;
            int end = (i == numThreads - 1) ? a.length : start + partitionSize;

            threads[i] = new WorkerThread(a, start, end, k);
            threads[i].start();
        }

        // Wait for all the threads to complete
        int[][] localResults = new int[numThreads][k];
        for (int i = 0; i < numThreads; ++i) {
            threads[i].join();
            localResults[i] = threads[i].getResult();
        }

        // Merge the results
        int[] merged = new int[numThreads * k];
        int index = 0;
        for (int[] localResult : localResults) {
            for (int value : localResult) {
                merged[index++] = value;
            }
        }
        return findKLargest(merged, k);
    }


    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.out.println("Params: java KLargestNumbers <n> <k>");
            return;
        }

        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        int numThreads = Runtime.getRuntime().availableProcessors();

        Random random = new Random(7363);
        int[] a = new int[n];
        for (int i = 0; i < n; ++i) {
            a[i] = random.nextInt(n);
        }

        double[] runtimesA2 = new double[7];
        double[] runtimesA1 = new double[7];
        double[] runtimesA3 = new double[7];

        for (int i = 0; i < 7; ++i) {
            double startTime = System.nanoTime();
            findKLargest(a.clone(), k);
            double endTime = System.nanoTime();
            double durationA2 = (endTime - startTime) / 1_000_000;
            runtimesA2[i] = durationA2;
        }

        for (int i = 0; i < 7; ++i) {
            double startTime = System.nanoTime();
            int[] aSorted = a.clone();
            Arrays.sort(aSorted);
            double endTime = System.nanoTime();
            double durationA1 = (endTime - startTime) / 1_000_000;
            runtimesA1[i] = durationA1;
        }

        for (int i = 0; i < 7; ++i) {
            double startTime = System.nanoTime();
            findKLargestParallel(a.clone(), k, numThreads);
            double endTime = System.nanoTime();
            double durationA3 = (endTime - startTime) / 1_000_000;
            runtimesA3[i] = durationA3;

        }

        int[] resultA2 = findKLargest(a.clone(), k);
        int[] aSorted = a.clone();
        int[] resultA3 = findKLargestParallel(a.clone(), k, numThreads);
        Arrays.sort(aSorted);
        int[] resultA1 = Arrays.copyOfRange(aSorted, aSorted.length - k, aSorted.length);
        Arrays.sort(resultA2);
        Arrays.sort(resultA3);
        boolean isCorrect = Arrays.equals(resultA2, resultA1) && Arrays.equals(resultA3,resultA1);

        Arrays.sort(runtimesA2);
        Arrays.sort(runtimesA1);
        double medianA2 = runtimesA2[runtimesA2.length / 2];
        double medianA1 = runtimesA1[runtimesA1.length / 2];
        double medianA3 = runtimesA3[runtimesA3.length / 2];

        System.out.println(Arrays.toString(runtimesA1));
        System.out.println("---------------------------------------------------------------------------");
        System.out.println(Arrays.toString(runtimesA2));
        System.out.println("---------------------------------------------------------------------------");
        System.out.println(Arrays.toString(runtimesA3));
        double speedupA2AgainstA1 = medianA1 / medianA2;
        double speedupA3AgainstA2 = medianA2 / medianA3;

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("n = " + n + ", k = " + k);
        System.out.println("Median A1 runtime of 7 runs: " + medianA1 + " ms");
        System.out.println("Median A2 runtime of 7 runs: " + medianA2 + " ms");
        System.out.println("Median A3 runtime of 7 runs: " + medianA3 + " ms");
        System.out.println("Number of threads: " + numThreads);

        System.out.println("Correct: " + isCorrect);
        System.out.println("SpeedupA2vA1: " + speedupA2AgainstA1);
        System.out.println("SpeedupA3vA2: " + speedupA3AgainstA2);
        System.out.println("---------------------------------------------------------------------------");
    }
}