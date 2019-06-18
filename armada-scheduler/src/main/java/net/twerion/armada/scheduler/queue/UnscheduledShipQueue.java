package net.twerion.armada.scheduler.queue;

import net.twerion.armada.Ship;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface UnscheduledShipQueue {

  void reschedule(Ship ship);

  CompletableFuture<?> rescheduleAsync(Ship ship);

  CompletableFuture<?> rescheduleAsync(Ship ship, Executor executor);

  Optional<Ship> pull();

  CompletableFuture<Ship> pullAsync();

  CompletableFuture<Ship> pullAsync(Executor executor);
}