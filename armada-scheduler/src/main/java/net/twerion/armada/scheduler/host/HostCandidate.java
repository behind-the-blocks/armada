package net.twerion.armada.scheduler.host;

import com.google.common.base.Preconditions;
import net.twerion.armada.LabelSetFilter;
import net.twerion.armada.Node;

public final class HostCandidate {
  private Node node;
  private LabelSetFilter shipFilter;

  private HostCandidate(Node node, LabelSetFilter shipFilter) {
    this.node = node;
    this.shipFilter = shipFilter;
  }

  public Node node() {
    return this.node;
  }

  public LabelSetFilter shipFilter() {
    return shipFilter;
  }

  public static HostCandidate of(Node node) {
    Preconditions.checkNotNull(node);
    return new HostCandidate(node, LabelSetFilter.of(node.getShipSelectors()));
  }
}
