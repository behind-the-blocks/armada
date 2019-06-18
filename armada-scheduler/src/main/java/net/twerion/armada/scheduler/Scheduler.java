package net.twerion.armada.scheduler;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import net.twerion.armada.Node;
import net.twerion.armada.Ship;
import net.twerion.armada.ShipBlueprint;
import net.twerion.armada.ShipLifecycleStage;
import net.twerion.armada.scheduler.filter.FilteredShipBlueprint;
import net.twerion.armada.scheduler.filter.HostFilter;
import net.twerion.armada.scheduler.host.HostCandidate;
import net.twerion.armada.scheduler.host.HostLister;
import net.twerion.armada.scheduler.host.HostShipAssigner;
import net.twerion.armada.scheduler.host.PrioritizedHostCandidate;
import net.twerion.armada.scheduler.priority.HostPrioritizer;

public final class Scheduler {
  private Logger logger;
  private HostFilter filter;
  private HostLister hostLister;
  private HostPrioritizer prioritizer;
  private HostShipAssigner shipAssigner;
  private SchedulerConfig config;

  Scheduler(
      Logger logger,
      HostFilter filter,
      HostLister lister,
      SchedulerConfig config,
      HostPrioritizer prioritizer,
      HostShipAssigner shipAssigner
  ) {
    this.logger = logger;
    this.filter = filter;
    this.config = config;
    this.hostLister = lister;
    this.shipAssigner = shipAssigner;
    this.prioritizer = prioritizer;
  }

  public void schedule(Ship ship) {
    if (ship.getLifecycleStage() != ShipLifecycleStage.SCHEDULED) {
      return;
    }
    Optional<Node> foundHost = findHost(ship.getBlueprint());
    // Can not use anything other than ifPresent()
    if (!foundHost.isPresent()) {
      noHostFound(ship);
      return;
    }
    Node host = foundHost.get();
    assignShipToHost(host, ship);
  }

  private void assignShipToHost(Node host, Ship ship) {
    logger.info("Assigning ship {} to host {}", ship.getId(), host.getId());
    shipAssigner.assign(host, ship);
  }

  private void noHostFound(Ship ship) {
    logger.info("No host found for ship {}", ship);
    // TODO(merlinosayimwen): Reschedule ship in a way that
    // makes it not pop up in the schedulers queue soon.
  }

  public Optional<Node> findHost(ShipBlueprint blueprint) {
    Stream<HostCandidate> suitable = findSuitableNodes(
      blueprint, config.filteredHostCandidateLimit());

    Stream<HostCandidate> best = prioritizeNodes(suitable, blueprint,
      config.prioritizedHostCandidateLimit());

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

  private Stream<HostCandidate> prioritizeNodes(
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