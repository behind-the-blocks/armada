package net.twerion.armada.scheduler.filter;

import net.twerion.armada.scheduler.host.HostCandidate;

public final class NodeShipSelectorFilter implements HostFilter {

  @Override
  public boolean filter(HostCandidate candidate, FilteredShipBlueprint ship) {
    return candidate.shipFilter().filter(ship.blueprint().getLabels());
  }
}
