package net.twerion.armada.scheduler.reschedule;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.twerion.armada.Ship;

public interface ShipRescheduler {

  void reschedule(Ship ship);

  CompletableFuture<?> rescheduleAsync(Ship ship);

  CompletableFuture<?> rescheduleAsync(Ship ship, Executor executor);
}
