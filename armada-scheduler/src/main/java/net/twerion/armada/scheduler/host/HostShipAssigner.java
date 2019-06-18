package net.twerion.armada.scheduler.host;

import net.twerion.armada.Node;
import net.twerion.armada.Ship;

public interface HostShipAssigner {

  void assign(Node node, Ship ship);
}