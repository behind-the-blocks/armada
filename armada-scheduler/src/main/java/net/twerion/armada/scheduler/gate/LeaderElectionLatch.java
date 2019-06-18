package net.twerion.armada.scheduler.gate;

import java.io.IOException;

import com.google.common.base.Preconditions;

import org.apache.curator.framework.recipes.leader.LeaderLatch;

public final class LeaderElectionLatch implements SchedulerStartLatch {
  private LeaderLatch latch;

  private LeaderElectionLatch(LeaderLatch latch) {
    this.latch = latch;
  }

  @Override
  public void awaitStartPermit() throws InterruptedException, IOException {
    latch.await();
  }

  @Override
  public void close() throws IOException {
    latch.close();
  }

  public static LeaderElectionLatch of(LeaderLatch latch) {
    Preconditions.checkNotNull(latch);
    return new LeaderElectionLatch(latch);
  }
}