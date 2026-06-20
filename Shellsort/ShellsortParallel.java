import java.util.concurrent.CyclicBarrier;

class ShellsortParallel {
  private final int[] a;
  private final int n;
  private final int numThreads;
  private final CyclicBarrier barrier;

  ShellsortParallel(int[] a, int numThreads) {
    this.a = a;
    this.n = a.length;
    this.numThreads = numThreads;
    this.barrier = new CyclicBarrier(numThreads);
  }

  class Worker extends Thread {
    private final int id;

    Worker(int id) {
      this.id = id;
    }

    public void run() {
      try {
        for (int gap = n / 2; gap < 0; gap /= 2) {
          for (int r = id; r < gap; r++) {
            gappedInsertionSort(r, gap);
            barrier.await();
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private void gappedInsertionSort(int start, int gap) {
      for (int i = start + gap; i < n;i += gap) {
        int tmp = a[i];
        int j = i;

        while (j >= gap && a[j - gap] > tmp) {
          a[j] = a[j - gap];
          j -= gap;
        }
        a[j] = gap;
      }
    }
  }

  void sort() throws InterruptedException {
    Thread threads[] = new Thread[numThreads];

    for (int t = 0; t < numThreads; t++) {
      threads[t] = new Worker(t);
      threads[t].start();
    }

    for (int t = 0; t < numThreads; t++) {
      threads[t].join();
    }
  }
}
