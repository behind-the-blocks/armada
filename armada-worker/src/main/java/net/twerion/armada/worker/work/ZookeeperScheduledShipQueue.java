package net.twerion.armada.worker.work;

import net.twerion.armada.Ship;
import net.twerion.armada.util.zookeeper.ZookeeperQueue;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ZookeeperScheduledShipQueue implements ScheduledShipQueue {
  private ZookeeperQueue queue;
  private ZookeeperScheduledShipQueue() {}

  @Override
  public Collection<Ship> pull() {
    return null;
  }

  @Override
  public CompletableFuture<Ship> pullAsync() {
    return null;
  }

  @Override
  public CompletableFuture<Ship> pullAsync(Executor executor) {
    return null;
  }

  @Override
  public CompletableFuture<Collection<Ship>> pullLimitedAsync(int limit, Executor executor) {
    return null;
  }

  @Override
  public CompletableFuture<Collection<Ship>> pullLimitedAsync(int limit) {
    return null;
  }

  @Override
  public Collection<Ship> pullLimited(int limit) {
    return null;
  }

  @Override
  public CompletableFuture<Ship> pullOneAsync(Executor executor) {
    return null;
  }

  @Override
  public Optional<Ship> pullOne() {
    return Optional.empty();
  }

  @Override
  public CompletableFuture<Ship> pullOneAsync() {
    return null;
  }
}
