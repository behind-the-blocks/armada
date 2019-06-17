package net.twerion.armada.scheduler.filter;

import net.twerion.armada.scheduler.host.HostCandidate;

public interface HostFilter {

  boolean filter(HostCandidate candidate, FilteredShipBlueprint ship);
}
