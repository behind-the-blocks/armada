package net.twerion.armada.scheduler.gate;

public final class OpenStartLatch implements SchedulerStartLatch {
  private OpenStartLatch() {}

  @Override
  public void awaitStartPermit() {}

  @Override
  public void close() {}

  public static OpenStartLatch create() {
    return new OpenStartLatch();
  }
}
