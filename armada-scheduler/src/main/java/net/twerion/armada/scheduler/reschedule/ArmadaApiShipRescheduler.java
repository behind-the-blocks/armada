package net.twerion.armada.scheduler.reschedule;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.Ship;
import net.twerion.armada.api.RescheduleShipRequest;
import net.twerion.armada.api.RescheduleShipResponse;
import net.twerion.armada.api.ShipServiceGrpc;

public class ArmadaApiShipRescheduler implements ShipRescheduler{
  private static Logger LOG = LogManager.getLogger(ArmadaApiShipRescheduler.class);

  private Executor fallbackExecutor;
  private ShipServiceGrpc.ShipServiceBlockingStub shipService;
  private RescheduleFailureStrategy rescheduleFailureStrategy;

  @Inject
  private ArmadaApiShipRescheduler(
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
}
