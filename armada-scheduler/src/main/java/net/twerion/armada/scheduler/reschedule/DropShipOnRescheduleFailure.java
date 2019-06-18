package net.twerion.armada.scheduler.reschedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.Ship;

public class DropShipOnRescheduleFailure implements RescheduleFailureStrategy {
  private Logger LOG = LogManager.getLogger(DropShipOnRescheduleFailure.class);

  @Override
  public void onFailure(Ship ship) {
    LOG.warn("Dropping ship {}, because it could not be rescheduled", ship.getId());
  }
}