package net.twerion.armada.scheduler.reschedule;

import net.twerion.armada.Ship;

public interface RescheduleFailureStrategy {

  void onFailure(Ship ship);
}
