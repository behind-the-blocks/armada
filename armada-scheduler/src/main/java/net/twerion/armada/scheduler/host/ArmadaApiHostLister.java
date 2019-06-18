package net.twerion.armada.scheduler.host;

import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import net.twerion.armada.Node;
import net.twerion.armada.api.NodeServiceGrpc;
import net.twerion.armada.api.ListNodesRequest;
import net.twerion.armada.api.ListNodesResponse;

public final class ArmadaApiHostLister implements HostLister {
  private Logger logger;
  private NodeServiceGrpc.NodeServiceBlockingStub nodeService;

  private ArmadaApiHostLister(
    Logger logger, NodeServiceGrpc.NodeServiceBlockingStub nodeService) {

    this.logger = logger;
    this.nodeService = nodeService;
  }

  @Override
  public Stream<Node> listHosts() {
    ListNodesRequest request = ListNodesRequest.newBuilder().build();
    return request(request);
  }

  @Override
  public Stream<Node> listHostsWithLimit(int limit) {
    ListNodesRequest request = ListNodesRequest.newBuilder()
      .setLimit(limit)
      .build();
    return request(request);
  }

  private Stream<Node> request(ListNodesRequest request) {
    try {
      ListNodesResponse response = nodeService.list(request);
      return response.getNodesList().stream();
    } catch (Exception rpcFailure) {
      logger.error("Failed listing nodes", rpcFailure);
      return Stream.empty();
    }
  }
}
