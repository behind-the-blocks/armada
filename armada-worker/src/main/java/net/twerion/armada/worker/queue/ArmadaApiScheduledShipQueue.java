package net.twerion.armada.worker.queue;

import java.util.Collection;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.Ship;
import net.twerion.armada.ShipLifecycleStage;
import net.twerion.armada.api.ListShipsOfNodeRequest;
import net.twerion.armada.api.ListShipsOfNodeResponse;
import net.twerion.armada.api.NodeServiceGrpc;

public final class ArmadaApiScheduledShipQueue implements ScheduledShipQueue {
  private static final Logger LOG = LogManager.getLogger(ArmadaApiScheduledShipQueue.class);

  private Executor fallbackExecutor;
  private NodeServiceGrpc.NodeServiceBlockingStub nodeService;

  @Inject
  private ArmadaApiScheduledShipQueue(
    Executor fallbackExecutor,
    NodeServiceGrpc.NodeServiceBlockingStub nodeService
  ) {
    this.fallbackExecutor = fallbackExecutor;
    this.nodeService = nodeService;
  }

  @Override
  public Collection<Ship> pull() {
    return null;
  }

  @Override
  public CompletableFuture<Ship> pullAsync(Executor executor) {
    return null;
  }

  @Override
  public CompletableFuture<Ship> pullAsync() {
    return null;
  }

  @Override
  public Collection<Ship> pullLimited(int limit) {
    return null;
  }

  @Override
  public CompletableFuture<Collection<Ship>> pullLimitedAsync(int limit) {
    return null;
  }

  @Override
  public CompletableFuture<Collection<Ship>> pullLimitedAsync(int limit, Executor executor) {
    return null;
  }

  @Override
  public Optional<Ship> pullOne() {
    ListShipsOfNodeRequest request = ListShipsOfNodeRequest
      .newBuilder()
      .setLifecycleStage(ShipLifecycleStage.SCHEDULED)
      .setLimit(1)
      .build();

    try {
      ListShipsOfNodeResponse response = nodeService.listShipsOfNode(request);
      if (response.getShipsCount() != 0) {
        return Optional.ofNullable(response.getShips(0));
      }
      return Optional.empty();
    } catch (Exception rpcFailure) {
      LOG.error("Failed listing scheduled nodes");
      return Optional.empty();
    }
  }

  @Override
  public CompletableFuture<Ship> pullOneAsync() {
    return pullOneAsync(fallbackExecutor);
  }

  @Override
  public CompletableFuture<Ship> pullOneAsync(Executor executor) {
    CompletableFuture<Ship> future = new CompletableFuture<>();
    executor.execute(() -> pullOneAndComplete(future));
    return future;
  }

  private void pullOneAndComplete(CompletableFuture<Ship> future) {
    Optional<Ship> ship = pullOne();
    if (ship.isPresent()) {
      future.complete(ship.get());
      return;
    }
    future.completeExceptionally(new NoSuchElementException());
  }

}
