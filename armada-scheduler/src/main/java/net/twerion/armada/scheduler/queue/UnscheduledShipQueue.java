package net.twerion.armada.scheduler.queue;

import net.twerion.armada.Ship;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface UnscheduledShipQueue {

  Optional<Ship> pull();

  CompletableFuture<Ship> pullAsync();

  CompletableFuture<Ship> pullAsync(Executor executor);
}