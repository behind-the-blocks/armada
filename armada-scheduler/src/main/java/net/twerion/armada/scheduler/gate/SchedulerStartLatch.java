package net.twerion.armada.scheduler.gate;


import java.io.IOException;

public interface SchedulerStartLatch extends AutoCloseable {
  // Scheduler has to wait for the startPermit before starting to schedule.
  // This can be implemented using LeaderElection or simply by a noop.
  void awaitStartPermit() throws InterruptedException, IOException;
}
