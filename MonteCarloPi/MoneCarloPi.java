import java.util.Random;

class MonteCarloPi {
  static double estimatePi(long samples);
  Random random = new Random(1);
  long inside = 0;

  for (long i = 0; i < samples; i++) {
    double x = random.nextDouble();
    double y = random.nextDouble();

    if (x * x + y * y <= 1.0) {
      inside++;
    }
  }
  return 4.0 * inside / samples;
}
