package net.twerion.armada.scheduler.reschedule;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import net.twerion.armada.Ship;
import net.twerion.armada.scheduler.queue.ShipQueue;

public final class ShipQueueRescheduler implements ShipRescheduler {
  private ShipQueue queue;
  private Executor fallbackExecutor;

  @Inject
  private ShipQueueRescheduler(ShipQueue queue, Executor fallbackExecutor) {
    this.queue = queue;
    this.fallbackExecutor = fallbackExecutor;
  }

  @Override
  public void reschedule(Ship ship) {
    queue.addLast(ship);
  }

  @Override
  public CompletableFuture<?> rescheduleAsync(Ship ship) {
    return rescheduleAsync(ship, fallbackExecutor);
  }

  @Override
  public CompletableFuture<?> rescheduleAsync(Ship ship, Executor executor) {
    CompletableFuture<?> future = new CompletableFuture<>();
    executor.execute(() -> rescheduleAndComplete(ship, future));
    return future;
  }

  private void rescheduleAndComplete(Ship ship, CompletableFuture<?> future) {
    reschedule(ship);
    future.complete(null);
  }
}
