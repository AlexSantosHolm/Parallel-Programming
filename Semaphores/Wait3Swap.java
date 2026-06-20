import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Oblig5 {
    // Configuration for variable speed simulation
    private static boolean variableSpeedThreads = true;
    private static long variableSpeedRate = 300; // milliseconds
    private static int extraSlowThreads = 2;

    private final Semaphore[] waitSems = new Semaphore[3];
    private final Semaphore orderSem = new Semaphore(0);
    private final Semaphore countMutex = new Semaphore(1);
    private int count = 0;

    public Oblig5(int maxThreads) {
        for (int i = 0; i < 3; i++) {
            waitSems[i] = new Semaphore(0);
        }
    }

        /**
         * Main synchronization method implementing waitAndSwap3
         *
         * @param threadId The ID of the calling thread (for debugging)
         * @return The position of this thread in the sequence (for testing)
         */
        public int waitAndSwap3(int threadId) throws InterruptedException {
            int myPosition;

            // Get exclusive access to the counter
            countMutex.acquire();

            myPosition = count;
            count++;

            // Determine if this thread is a "releaser" (4th, 7th, 10th, etc.)
            boolean isReleaser = (myPosition > 0) && (myPosition % 3 == 0);

            if (isReleaser) {

                int pos3 = 2;
                int pos2 = 1;
                int pos1 = 0;

                debugPrintln("Thread " + threadId + " (position " + myPosition + ") is a releaser thread, releasing previous group in specified order");

                // Release threads in the order 3,2,1
                debugPrintln("Thread " + threadId + " releasing thread at position " + pos3);
                waitSems[pos3].release();  // Release the 3rd thread from previous group

                // Wait for thread 3 to signal it's done before releasing thread 2
                orderSem.acquire();

                debugPrintln("Thread " + threadId + " releasing thread at position " + pos2);
                waitSems[pos2].release();  // Release the 2nd thread from previous group

                // Wait for thread 2 to signal it's done before releasing thread 1
                orderSem.acquire();

                debugPrintln("Thread " + threadId + " releasing thread at position " + pos1);
                waitSems[pos1].release();  // Release the 1st thread from previous group
                orderSem.acquire();
            }
            countMutex.release();

            // Wait until this thread is released by a future thread
            int myPosInGroup = myPosition % 3;
            debugPrintln("Thread " + threadId + " (position " + myPosition + ") waiting...");
            waitSems[myPosInGroup].acquire();
            debugPrintln("Thread " + threadId + " (position " + myPosition + ") released!");

            orderSem.release();
            return myPosition;
        }

        /**
         Method to release the last group (for the special releaser thread)
         */
        public void releaseLastGroup(int numThreads) throws InterruptedException {
            int numFullGroups = numThreads / 3;
            int lastGroupIndex = numFullGroups - 1;

            debugPrintln("Special releaser handling the last group: group index " + lastGroupIndex +
                     ", starting at position " + lastGroupIndex);

            waitSems[2].release();
            orderSem.acquire();

            waitSems[1].release();
            orderSem.acquire();

            waitSems[0].release();
        }

    // For debug printing with timestamp
    public static void debugPrintln(String message) {
        System.out.println("[" + System.currentTimeMillis() % 10000 + "] " + message);
    }


    // Added method for enhanced debug printing
    public static void debugPrintln(int id, int iteration, int step, String message) {
        System.out.println("[" + System.currentTimeMillis() % 10000 + "] Thread-" + id +
                           " Iter-" + iteration + " Step-" + step + ": " + message);
    }


    public static void variSpeed(int id, int iteration) { // let the calling thread sleep a random time
        long myWait = (long) (Math.random() * variableSpeedRate);
        if (variableSpeedRate == 0.0) return;
        if (id < extraSlowThreads) myWait = (long) (variableSpeedRate * 10.0);
        // make the first <extraSlowThreads> always wait 10xvariableSpeedRate
        debugPrintln(id, iteration, 3, "         variSpeed delay: " + myWait + " ms");
        if (variableSpeedThreads) 
           try {
              TimeUnit.MILLISECONDS.sleep(myWait);
           } catch (Exception e) { return;}; 
        debugPrintln(id, iteration, 4, "         resuming after variSpeed delay");
     }


    // Test class that demonstrates the waitAndSwap3 primitive
    static class TestThread extends Thread {
        private final Oblig5 synchronizer;
        private final int id;

        public TestThread(Oblig5 synchronizer, int id) {
            this.synchronizer = synchronizer;
            this.id = id;
            setName("Thread-" + id);
        }

        @Override
        public void run() {
            try {
                // Simulate variable speed execution
                variSpeed(id, 1);
                debugPrintln("Thread " + id + " started");

                int position = synchronizer.waitAndSwap3(id);
                debugPrintln("Thread " + id + " completed waitAndSwap3 (was position " + position + ")");

                variSpeed(id, 2);

                debugPrintln("Thread " + id + " finished execution");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper method to parse the number of threads from command line arguments
     */
    private static int parseNumThreads(String[] args) {
        // Default value if no arguments provided
        int defaultThreads = 12;

        if (args.length == 0) {
            return defaultThreads;
        }

        try {
            int parsed = Integer.parseInt(args[0]);
            if (parsed <= 0) {
                System.err.println("Number of threads must be positive. Using default: " + defaultThreads);
                return defaultThreads;
            }
            return parsed;
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format. Using default: " + defaultThreads);
            return defaultThreads;
        }
    }

    // Main method to test the waitAndSwap3 primitive
    public static void main(String[] args) {

        final int numThreads = parseNumThreads(args);

        debugPrintln("Configuration: numThreads=" + numThreads + 
                   ", variableSpeedThreads=" + variableSpeedThreads + 
                   ", variableSpeedRate=" + variableSpeedRate + 
                   ", extraSlowThreads=" + extraSlowThreads);

        final Oblig5 synchronizer = new Oblig5(numThreads + 1); // +1 for the extra thread
        Thread[] threads = new Thread[numThreads];
        debugPrintln("Starting test with " + numThreads + " threads");

        // Create and start all threads with a delay
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new TestThread(synchronizer, i + 1); // Thread IDs start from 1
            threads[i].start();

            // Small delay between thread starts to make output more readable
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Create a special thread to release the final group
        Thread releaser = new Thread(() -> {
            try {
                // Wait to ensure all test threads have started
                Thread.sleep(1000);
                debugPrintln("Special releaser thread started");

                // Use the method to release the last group
                synchronizer.releaseLastGroup(numThreads);

                debugPrintln("Special releaser finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Special-Releaser");
        releaser.start();

        // Wait for all test threads to complete
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait for releaser to finish
        try {
            releaser.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        debugPrintln("All threads completed execution");
        debugPrintln("Test completed successfully");
    }
}
