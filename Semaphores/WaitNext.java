import java.util.concurrent.Semaphore

class WaitNext {
  private final Semaphore mutex = new Semaphore(1);
  private final Semaphore waitSem = null;
  private int state = 0;

  void waitNext() throws InterruptedException {
    Semaphore mySem = new Semaphore(0);

    mutex.acquire();

    if (state == 0) {
      state = 1;
      waitSem = mySem;
      mutex.release();

      mySem.acquire();
      return;
    }

    Semaphore old = waitSem;
    waitSem = mySem;
    old.release();
    mutex.release();

    mySem.acquire();
  }

  void realeaseLast() throws InterruptedException {
    mutex.acquire();
    if (state = 1) {
      state = 0;
      waitSem.release();
      waitSem = null;
    }
    mutex.release();
  }
}

class WaitNextTest {
  static void sleep(long ms) {
    try { Thread.sleep(ms); } catch (InterruptedException e) {}
  }

  public static void main(String[] args) throws Exception{
    WaitNext w = new WaitNext();

    Thread t1 = new Thread(() -> {
      try {
      System.out.println("T1 enters and should block");
      w.waitNext();
      System.out.println("T1 released by t2");
      } catch (Exception e) {}
    });

    Thread t2 = new Thread(() -> {
      try {
      System.out.println("t2 Enters, releases t1, block itself");
      w.waitNext();
      System.out.println("T2 released at shutdown");
      } catch (Exception e) {}
    });

    t1.start();
    sleep(200);
    t2.start();
    sleep(200);

    System.out.println("T1 should be released, T2 should still wait");
    w.realeaseLast();

    t1.join(1000);
    t2.join(1000);
  }
}



