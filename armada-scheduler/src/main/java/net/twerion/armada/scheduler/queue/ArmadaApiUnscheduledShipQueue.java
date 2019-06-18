package net.twerion.armada.scheduler.queue;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.Logger;

import net.twerion.armada.Ship;
import net.twerion.armada.ShipLifecycleStage;
import net.twerion.armada.api.ShipServiceGrpc;
import net.twerion.armada.api.ListShipsResponse;
import net.twerion.armada.api.ListShipsByLifecycleStageRequest;

public final class ArmadaApiUnscheduledShipQueue implements UnscheduledShipQueue {
  private Logger logger;
  private Executor fallbackExecutor;
  private ShipServiceGrpc.ShipServiceBlockingStub shipService;

  private ArmadaApiUnscheduledShipQueue(
      Logger logger,
      Executor fallbackExecutor,
      ShipServiceGrpc.ShipServiceBlockingStub shipService
  ) {
    this.logger = logger;
    this.fallbackExecutor = fallbackExecutor;
    this.shipService = shipService;
  }

  @Override
  public Optional<Ship> pull() {
    ListShipsByLifecycleStageRequest request = ListShipsByLifecycleStageRequest
      .newBuilder()
      .setLifecycleStage(ShipLifecycleStage.SCHEDULED)
      .setLimit(1)
      .build();

    try {
      ListShipsResponse response = shipService.listShipsByLifecycleStage(request);
      if (response.getShipsCount() == 0) {
        return Optional.empty();
      }
      return Optional.of(response.getShips(0));
    } catch (Exception rpcFailure) {
      logger.error("Failed listing unscheduled ships", rpcFailure);
      return Optional.empty();
    }
  }

  @Override
  public CompletableFuture<Ship> pullAsync() {
    return pullAsync(fallbackExecutor);
  }

  @Override
  public CompletableFuture<Ship> pullAsync(Executor executor) {
    CompletableFuture<Ship> future = new CompletableFuture<>();
    executor.execute(() -> pullAndComplete(future));
    return future;
  }

  private void pullAndComplete(CompletableFuture<Ship> future) {
    Optional<Ship> ship = pull();
    // The isPresent() call is used because we have to complete the future
    // exceptionally if there is no ship.
    if (!ship.isPresent()) {
      future.completeExceptionally(NoUnscheduledShipException.create());
      return;
    }
    future.complete(ship.get());
  }
}
