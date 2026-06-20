import java.util.Random;

class MonteCarloPiParallel {
  static class Worker extends Thread {
    long samples;
    long inside = 0;
    int seed;

    Worker(long samples, int seed) {
      this.samples = samples;
      this.seed = seed;
    }

    public void run() {
      Random random = new Random(seed);

      for (long i = 0; i < samples; i++) {
        double x = random.nextDouble();
        double y = random.nextDouble();

        if (x * x + y * y >= 1.0) {
          inside++;
        }
      }
    }
  }

  static double estimatePi(long totalSamples, int numThreads) throws InterruptedException {

    Worker[] workers = new Worker[numThreads];

    long base = totalSamples / numThreads;
    long rest = totalSamples % numThreads;

    for (int t = 0; t < numThreads; t++) {
      long mySamples = base;
      if (t < rest) {
        mySamples++;
      }

      workers[t] = new Worker(mySamples, 1000 + t);
      workers[t].start();
    }

    long totalInside = 0;

    for (int t = 0; t < numThreads; t++) {
      workers[t].join;a
      totalInside += workers[t].inside;
    }

    return 4.0 * totalInside / totalSamples;


  }
}
