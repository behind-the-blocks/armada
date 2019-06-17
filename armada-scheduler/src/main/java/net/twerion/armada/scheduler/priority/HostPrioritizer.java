package net.twerion.armada.scheduler.priority;

import net.twerion.armada.ShipBlueprint;
import net.twerion.armada.scheduler.host.HostCandidate;

public interface HostPrioritizer {

  float calculatePriority(HostCandidate candidate, ShipBlueprint blueprint);
}