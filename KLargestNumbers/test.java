import java.util.Arrays;
import java.util.Random;

public class test {

    // Insertion sort in descending order
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

    // Algorithm A2 to find the k largest numbers
    public static int[] findKLargest(int[] a, int k) {
        if (k <= 0 || k > a.length) {
            throw new IllegalArgumentException("k must be between 1 and a.length");
        }

        // Sort the first k elements in descending order
        insertSortDec(a, 0, k - 1);

        // Compare the rest of the array with the k-th element
        for (int j = k; j < a.length; ++j) {
            if (a[j] > a[k - 1]) {
                // Replace the smallest of the k largest elements
                a[k - 1] = a[j];

                // Insert the new element into the sorted subarray a[0..k-2]
                int t = a[k - 1];
                int i = k - 2;
                while (i >= 0 && a[i] < t) {
                    a[i + 1] = a[i];
                    i--;
                }
                a[i + 1] = t;
            }
        }

        // Return the k largest elements
        return Arrays.copyOfRange(a, 0, k);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java KLargestNumbers <n> <k>");
            return;
        }

        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);

        // Generate random numbers
        Random random = new Random(7363);
        int[] a = new int[n];
        for (int i = 0; i < n; ++i) {
            a[i] = random.nextInt(n);
        }

        // Print the input array
        System.out.println(Arrays.toString(a));
        System.out.println("-----------------------------------------");

        // Measure time for A2
        long startTime = System.nanoTime();
        int[] resultA2 = findKLargest(a.clone(), k);
        long endTime = System.nanoTime();
        long durationA2 = (endTime - startTime) / 1_000_000; // in milliseconds

        // Print result from A2
        System.out.println("Result from A2: " + Arrays.toString(resultA2));

        // Measure time for Arrays.sort
        startTime = System.nanoTime();
        int[] aSorted = a.clone();
        Arrays.sort(aSorted);
        int[] resultA1 = Arrays.copyOfRange(aSorted, aSorted.length - k, aSorted.length);
        endTime = System.nanoTime();
        long durationA1 = (endTime - startTime) / 1_000_000; // in milliseconds

        // Print result from Arrays.sort
        System.out.println("Result from A1: " + Arrays.toString(resultA1));

        // Verify correctness
        Arrays.sort(resultA2);
        Arrays.sort(resultA1);
        boolean isCorrect = Arrays.equals(resultA2, resultA1);

        // Output results
        System.out.println("n = " + n + ", k = " + k);
        System.out.println("A2 Time: " + durationA2 + " ms");
        System.out.println("A1 Time: " + durationA1 + " ms");
        System.out.println("Correct: " + isCorrect);
    }
}