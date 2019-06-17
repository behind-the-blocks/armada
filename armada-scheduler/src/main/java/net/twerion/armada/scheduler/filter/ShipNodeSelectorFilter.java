package net.twerion.armada.scheduler.filter;

import net.twerion.armada.scheduler.host.HostCandidate;

public final class ShipNodeSelectorFilter implements HostFilter {

  @Override
  public boolean filter(HostCandidate candidate, FilteredShipBlueprint ship) {
    return ship.nodeFilter().filter(candidate.node().getLabels());
  }
}
