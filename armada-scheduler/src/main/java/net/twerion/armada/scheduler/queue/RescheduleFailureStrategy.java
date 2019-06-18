package net.twerion.armada.scheduler.queue;

import net.twerion.armada.Ship;

public interface RescheduleFailureStrategy {

  void onFailure(Ship ship);
}
