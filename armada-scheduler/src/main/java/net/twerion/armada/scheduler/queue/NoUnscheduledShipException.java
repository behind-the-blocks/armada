package net.twerion.armada.scheduler.queue;

public final class NoUnscheduledShipException extends Exception {
  private NoUnscheduledShipException() {
    super(message());
  }

  private static String message () {
    return "the queue of unscheduled ships is empty";
  }

  public static NoUnscheduledShipException create() {
    return new NoUnscheduledShipException();
  }
}
