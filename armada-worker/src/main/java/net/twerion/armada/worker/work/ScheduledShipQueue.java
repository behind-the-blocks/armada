package net.twerion.armada.worker.work;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.twerion.armada.Ship;

public interface ScheduledShipQueue {

  Collection<Ship> pull();

  CompletableFuture<Ship> pullAsync();

  CompletableFuture<Ship> pullAsync(Executor executor);

  Collection<Ship> pullLimited(int limit);

  CompletableFuture<Collection<Ship>> pullLimitedAsync(int limit);

  CompletableFuture<Collection<Ship>> pullLimitedAsync(int limit, Executor executor);

  Optional<Ship> pullOne();

  CompletableFuture<Ship> pullOneAsync();

  CompletableFuture<Ship> pullOneAsync(Executor executor);
}
