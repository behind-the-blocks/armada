package net.twerion.armada.scheduler.reschedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.Ship;
import net.twerion.armada.api.DeleteShipRequest;
import net.twerion.armada.api.ShipServiceGrpc;

public class DeleteShipOnRescheduleFailure implements RescheduleFailureStrategy {
  private Logger LOG = LogManager.getLogger(DeleteShipOnRescheduleFailure.class);
  private ShipServiceGrpc.ShipServiceFutureStub shipService;

  @Override
  public void onFailure(Ship ship) {
    LOG.warn(
      "Deleting ship {}, because it could not be rescheduled", ship.getId());

    try {
      shipService.delete(DeleteShipRequest.newBuilder()
        .setShipId(ship.getId())
        .build()
      );
      LOG.info("Successfully deleted unscheduled ship {}", ship.getId());
    } catch (Exception rpcFailure) {
      String errorMessage = String.format(
        "Could not delete unscheduled ship %s", ship.getId()
      );
      LOG.error(errorMessage, rpcFailure);
      LOG.error("Ship will no longer be scheduled and may create a leak");
    }
  }
}
