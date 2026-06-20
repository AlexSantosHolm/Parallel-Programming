import java.util.concurrent.Semaphore

class PairUp {
  private final Semaphore mutex = new Semaphore[1];
  private final Semaphore waitSem = new Semaphore[0];
  private int waiting = 0;

  void pairUp() throws InterruptedException {
    mutex.acquire();

    if (waiting == 0) {
      waiting = 1;
      mutex.release();
        
      waitSem.acquire();
      return;
    }
    
    waiting = 0;
    waitSem.release();
    mutex.release();
  }
}


class PairUpTest {
  static void sleep(long ms) {
    try { Thread.sleep(ms); } catch (InterruptedException e) {}
  }

  public static void main(String[] args) throws Exception {
    PairUp p = new PairUp();

    Thread t1 = new Thread(() -> {
      try {
      System.out.println("T1 before PairUp");
      p.PairUp();
      System.out.println("T1 after PairUp");
      } catch (Exception e) {}
    });

    t1.start();
    t1.sleep(200);
    System.out.println("After 200ms: T1 should still be blocked");

    Thread t2 = new Thread(() -> {
      try {
      System.out.println("T2 before PairUp");
      p.PairUp();
      System.out.println("T2 after PairUp");
      } catch (Exception e) {}
    });

    t2.start();
    t1.join(1000);
    t2.join(1000);
    System.out.println("Both threads should be finished now");
  } 
}
