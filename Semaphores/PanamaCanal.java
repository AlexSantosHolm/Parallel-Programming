import java.util.*;
import java.util.concurrent.Semaphore;

class PanamaLock {
  private static class ShipWaiter {
    int id;
    int length;
    Semaphore enterSem = new Semaphore(0);
    Semaphore exitSem = new Semaphore(0);

    ShipWaiter(int id, int length) {
      this.id = id;
      this.length = length;
    }
  }

  private final int capacity;
  private int free;
  private boolean openLow = true;

  private final Semaphore mutex = new Semaphore(1);
  private final ArrayDeque<ShipWaiter> waiting = new ArrayDeque<>();
  private final ArrayList<ShipWaiter> inside = new ArrayList<>();

  PanamaLock(int capacity) {
    this.capacity = capacity;
    this.free = capacity;
  }

  void enterLock(int shipId, int shipLength) throws InterruptedException {
    ShipWaiter me = new ShipWaiter(shipId, shipLength);

    mutex.acquire();
    if (canEnterNow(shipLength)) {
      free -= shipLength;
      inside.add(me);
      System.out.println("Ship " + shipId + " enters");
      mutex.release();
    } else {
      waiting.addLast(me);
      System.out.println("Ship " + shipId + " is waiting");
      mutex.release();
      me.enterSem.acquire();
    }
    me.exitSem.acquire();
    System.out.println("Ship " + shipId " leaves high end");
  }

  private boolean canEnterNow(int shipLength) {
    return openLow && shipLength <= free;
  }

  void closeLock() throws InterruptedException {
    mutex.acquire();

    if (inside.isEmpty()) {
      wakeShipsThatFit();
      mutex.release();
      return;
    }

    openLow = false;
    System.out.println("Harbor master closes low end");
    System.out.println("Lock fills and open high end");
    ArrayList<ShipWaiter> leaving = new ArrayList<>(inside);
    inside.clear();

    for (ShipWaiter s : leaving) {
      s.exitSem.release();
    }

    free = capacity;
    openLow = true;
    System.out.println("Low end reopened");

    wakeShipsThatFit();
    mutex.release();
  }

  private void wakeShipsThatFit() {
    Iterator<ShipWaiter> it = waiting.iterator();
    while (it.hasNext()) {
      ShipWaiter s = it.next();
      if (openLow && s.length <= free) {
        free -= s.length;
        inside.add(s);
        it.remove();
        System.out.println("Ship " + shipId + " allowed to enter");
        s.enterSem.release();
      }
    }
  }
}
