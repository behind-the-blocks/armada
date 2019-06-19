package net.twerion.armada.scheduler.filter;

import com.google.common.annotations.VisibleForTesting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.Memory;
import net.twerion.armada.Node;
import net.twerion.armada.ResourceRequirements;
import net.twerion.armada.scheduler.host.HostCandidate;

public final class ResourceFilter implements HostFilter {
  private static final Logger LOG = LogManager.getLogger(ResourceFilter.class);

  @Override
  public boolean filter(HostCandidate candidate, FilteredShipBlueprint ship) {
    if (canFitResources(candidate.node(), ship.blueprint().getRequiredResources())) {
      return true;
    }
    LOG.trace("Node {} can not fit {}",
      candidate.node().getId(), ship.blueprint().getId());
    return false;
  }

  @VisibleForTesting
  boolean canFitResources(Node node, ResourceRequirements resources) {
    // Memory nodeMemory = node.getResources().getMemory();
    // return nodeMemory.getAvailable() > resources.getBytesOfMemory();
    return false;
  }
}