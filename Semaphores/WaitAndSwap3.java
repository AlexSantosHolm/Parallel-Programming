import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WaitAndSwap33 {

        // Configuration for variable speed simulation
        private static boolean variableSpeedThreads = true;
        private static long variableSpeedRate = 300; // milliseconds
        private static int extraSlowThreads = 2;

        private Semaphore[] waitSems;
        private Semaphore[] orderSems;
        private Semaphore countMutex = new Semaphore(1);
        private int count = 0;

        public WaitAndSwap33(int maxThreads) {
            // Initialize semaphores for all possible thread positions
            waitSems = new Semaphore[maxThreads];
            orderSems = new Semaphore[maxThreads];

            for (int i = 0; i < maxThreads; i++) {
                waitSems[i] = new Semaphore(0);
                orderSems[i] = new Semaphore(0);
            }
        }

        /**
         * Main synchronization method implementing waitAndSwap3 semantics
         *
         * @param threadId The ID of the calling thread (for debugging)
         * @return The position of this thread in the sequence (for testing)
         */
        public int waintAndSwap33(int threadId) throws InterruptedException {
            int myPosition;

            // Get exclusive access to the counter
            countMutex.acquire();

            myPosition = count;
            count++;

            // Determine if this thread is a "releaser" (4th, 7th, 10th, etc.)
            boolean isReleaser = (myPosition > 0) && (myPosition % 3 == 0);

            if (isReleaser) {
                // Calculate the positions of threads to release in previous group
                int releaseGroup = myPosition / 3 - 1;
                int pos3 = releaseGroup * 3 + 2;
                int pos2 = releaseGroup * 3 + 1;
                int pos1 = releaseGroup * 3;

                debugPrintln("Thread " + threadId + " (position " + myPosition + ") is a releaser thread, releasing previous group in specified order");

                // Release threads in the order 3,2,1
                debugPrintln("Thread " + threadId + " releasing thread at position " + pos3);
                waitSems[pos3].release();  // Release the 3rd thread from previous group

                // Wait for thread 3 to signal it's done before releasing thread 2
                orderSems[myPosition].acquire();

                debugPrintln("Thread " + threadId + " releasing thread at position " + pos2);
                waitSems[pos2].release();  // Release the 2nd thread from previous group

                // Wait for thread 2 to signal it's done before releasing thread 1
                orderSems[myPosition].acquire();

                debugPrintln("Thread " + threadId + " releasing thread at position " + pos1);
                waitSems[pos1].release();  // Release the 1st thread from previous group
            }
            countMutex.release();

            // Wait until this thread is released by a future thread
            debugPrintln("Thread " + threadId + " (position " + myPosition + ") waiting...");
            waitSems[myPosition].acquire();
            debugPrintln("Thread " + threadId + " (position " + myPosition + ") released!");

            // Perform ordered execution based on position within the group
            int myPosInGroup = myPosition % 3;

            // If we're the 3rd ord 2nd thread in a group
            if (myPosInGroup == 2 || myPosInGroup == 1) {
                // Signal the releaser that we're done so it can release next
                int myGroup = myPosition / 3;
                int releaserPos = (myGroup + 1) * 3;
                if (releaserPos < orderSems.length) {
                    orderSems[releaserPos].release();
                }
            }
            return myPosition;
        }

        /**
         Method to release the last group (for the special releaser thread)
         */
        public void releaseLastGroup(int numThreads) throws InterruptedException {
            int numFullGroups = numThreads / 3;
            int lastGroupIndex = numFullGroups - 1;
            int lastGroupStart = lastGroupIndex * 3; // First position in the last group

            debugPrintln("Special releaser handling the last group: group index " + lastGroupIndex +
                     ", starting at position " + lastGroupStart);

            // Make sure we don't try to release threads that have already been released
            if (lastGroupStart + 2 >= 0 && lastGroupStart + 2 < waitSems.length) {
                debugPrintln("Special releaser releasing thread at position " + (lastGroupStart + 2));
                waitSems[lastGroupStart + 2].release(); // Release the 3rd thread in last group

                orderSems[numThreads].acquire();

                debugPrintln("Special releaser releasing thread at position " + (lastGroupStart + 1));
                waitSems[lastGroupStart + 1].release(); // Release the 2nd thread in last group

                orderSems[numThreads].acquire();

                debugPrintln("Special releaser releasing thread at position " + lastGroupStart);
                waitSems[lastGroupStart].release(); // Release the 1st thread in last group
            } else {
                debugPrintln("No complete groups to release by special releaser");
            }
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
        private final WaitAndSwap33 synchronizer;
        private final int id;

        public TestThread(WaitAndSwap33 synchronizer, int id) {
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

                int position = synchronizer.waintAndSwap33(id);
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

        final WaitAndSwap33 synchronizer = new WaitAndSwap33(numThreads + 1); // +1 for the extra thread
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
