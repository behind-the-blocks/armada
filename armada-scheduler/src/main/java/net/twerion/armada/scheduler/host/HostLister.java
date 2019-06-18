package net.twerion.armada.scheduler.host;

import net.twerion.armada.Node;

import java.util.stream.Stream;

public interface HostLister {
  Stream<Node> listHosts();
  Stream<Node> listHostsWithLimit(int limit);
}
