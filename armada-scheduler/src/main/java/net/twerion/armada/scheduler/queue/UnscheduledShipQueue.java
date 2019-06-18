package net.twerion.armada.scheduler.queue;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.twerion.armada.Ship;

public interface UnscheduledShipQueue {


  Optional<Ship> pull();

  CompletableFuture<Ship> pullAsync();

  CompletableFuture<Ship> pullAsync(Executor executor);
}