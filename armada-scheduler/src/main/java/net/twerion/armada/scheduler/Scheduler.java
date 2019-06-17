package net.twerion.armada.scheduler;

import java.util.Optional;
import java.util.stream.Stream;

import net.twerion.armada.Node;
import net.twerion.armada.ShipBlueprint;
import net.twerion.armada.scheduler.filter.FilteredShipBlueprint;
import net.twerion.armada.scheduler.filter.HostFilter;
import net.twerion.armada.scheduler.host.HostCandidate;
import net.twerion.armada.scheduler.host.HostLister;
import net.twerion.armada.scheduler.host.PrioritizedHostCandidate;
import net.twerion.armada.scheduler.priority.HostPrioritizer;

public final class Scheduler {
  private HostFilter filter;
  private HostLister hostLister;
  private HostPrioritizer prioritizer;
  private int suitableNodeLimit;
  private int bestNodeLimit;

  Scheduler(
    HostFilter filter,
    HostLister lister,
    HostPrioritizer prioritizer,
    int suitableNodeLimit,
    int bestNodeLimit) {

    this.filter = filter;
    this.hostLister = lister;
    this.prioritizer = prioritizer;
    this.suitableNodeLimit = suitableNodeLimit;
    this.bestNodeLimit = bestNodeLimit;
  }

  public Optional<Node> findHost(ShipBlueprint blueprint) {
    Stream<HostCandidate> suitable = findSuitableNodes(blueprint, suitableNodeLimit);
    Stream<HostCandidate> best = findBestNodes(suitable, blueprint, bestNodeLimit);
    // TODO(merlinosayimwen): Use round robin?
    return best.findAny().map(HostCandidate::node);
  }

  private Stream<HostCandidate> findSuitableNodes(ShipBlueprint blueprint, int limit) {
    FilteredShipBlueprint filteredBlueprint = FilteredShipBlueprint.of(blueprint);
    return hostLister.listHosts()
      .parallel()
      .map(HostCandidate::of)
      .filter(candidate -> filter.filter(candidate, filteredBlueprint))
      .limit(limit);
  }

  private Stream<HostCandidate> findBestNodes(
    Stream<HostCandidate> nodes, ShipBlueprint blueprint, int limit) {

    return nodes
      .map(node -> prioritize(node, blueprint))
      .sorted()
      .map(PrioritizedHostCandidate::candidate)
      .limit(limit);
  }

  private PrioritizedHostCandidate prioritize(HostCandidate candidate, ShipBlueprint blueprint) {
    float priority = prioritizer.calculatePriority(candidate, blueprint);
    return PrioritizedHostCandidate.create(candidate, priority);
  }
}