package net.twerion.armada.scheduler.filter;

import javax.inject.Inject;

import com.google.common.annotations.VisibleForTesting;

import org.apache.logging.log4j.Logger;

import net.twerion.armada.Memory;
import net.twerion.armada.Node;
import net.twerion.armada.ResourceRequirements;
import net.twerion.armada.scheduler.host.HostCandidate;

public final class ResourceFilter implements HostFilter {
  private Logger logger;

  @Inject
  @VisibleForTesting
  ResourceFilter(Logger logger) {
    this.logger = logger;
  }

  @Override
  public boolean filter(HostCandidate candidate, FilteredShipBlueprint ship) {
    if (canFitResources(candidate.node(), ship.blueprint().getRequiredResources())) {
      return true;
    }
    logger.trace("Node {} can not fit {}", candidate.node().getId(), ship.blueprint().getId());
    return false;
  }

  @VisibleForTesting
  boolean canFitResources(Node node, ResourceRequirements resources) {
    Memory nodeMemory = node.getResources().getMemory();
    return nodeMemory.getAvailable() > resources.getBytesOfMemory();
  }
}