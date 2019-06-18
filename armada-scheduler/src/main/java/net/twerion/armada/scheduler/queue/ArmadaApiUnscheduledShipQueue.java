package net.twerion.armada.scheduler.queue;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.Ship;
import net.twerion.armada.ShipLifecycleStage;
import net.twerion.armada.api.ShipServiceGrpc;
import net.twerion.armada.api.ListShipsResponse;
import net.twerion.armada.api.RescheduleShipRequest;
import net.twerion.armada.api.RescheduleShipResponse;
import net.twerion.armada.api.ListShipsByLifecycleStageRequest;

public final class ArmadaApiUnscheduledShipQueue implements UnscheduledShipQueue {
  private static Logger LOG = LogManager.getLogger(ArmadaApiUnscheduledShipQueue.class);

  private Executor fallbackExecutor;
  private ShipServiceGrpc.ShipServiceBlockingStub shipService;
  private RescheduleFailureStrategy rescheduleFailureStrategy;

  @Inject
  private ArmadaApiUnscheduledShipQueue(
      Executor fallbackExecutor,
      ShipServiceGrpc.ShipServiceBlockingStub shipService,
      RescheduleFailureStrategy rescheduleFailureStrategy
  ) {
    this.shipService = shipService;
    this.fallbackExecutor = fallbackExecutor;
    this.rescheduleFailureStrategy = rescheduleFailureStrategy;
  }

  @Override
  public void reschedule(Ship ship) {
    RescheduleShipRequest request = RescheduleShipRequest
      .newBuilder()
      .setShipId(ship.getId())
      .build();

    try {
      RescheduleShipResponse response= shipService.reschedule(request);
      if (response.getSuccess()) {
        LOG.info("Successfully rescheduled ship {}.", ship.getId());
        return;
      }
      LOG.error("Failed rescheduling ship {}. Service responded with:", ship.getId());
      LOG.error(response.getErrorCode());
      if (response.getShipDiscarded()) {
        LOG.error("The ship has been discarded");
      }
    } catch (Exception rpcFailure) {
      LOG.error("Failed rescheduling ship {}", ship.getId());
      rescheduleFailureStrategy.onFailure(ship);
    }
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
      LOG.error("Failed listing unscheduled ships", rpcFailure);
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
