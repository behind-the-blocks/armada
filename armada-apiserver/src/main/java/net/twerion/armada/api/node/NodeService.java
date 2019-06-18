package net.twerion.armada.api.node;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import io.grpc.stub.StreamObserver;

import net.twerion.armada.Node;
import net.twerion.armada.api.CreateNodeRequest;
import net.twerion.armada.api.CreateNodeResponse;;
import net.twerion.armada.api.DeleteNodeRequest;
import net.twerion.armada.api.DeleteNodeResponse;;
import net.twerion.armada.api.UpdateNodeRequest;
import net.twerion.armada.api.UpdateNodeResponse;
import net.twerion.armada.api.FindNodeRequest;
import net.twerion.armada.api.FindNodeResponse;;
import net.twerion.armada.api.ListNodesRequest;
import net.twerion.armada.api.ListNodesResponse;
import net.twerion.armada.api.NodeServiceGrpc;

public final class NodeService extends NodeServiceGrpc.NodeServiceImplBase {
  private Executor executor;
  private NodeRepository repository;

  @Inject
  private NodeService(Executor executor, NodeRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  @Override
  public void create(
      CreateNodeRequest request,
      StreamObserver<CreateNodeResponse> responseObserver
  ) {
    super.create(request, responseObserver);
  }

  @Override
  public void delete(
      DeleteNodeRequest request,
      StreamObserver<DeleteNodeResponse> responseObserver
  ) {
    super.delete(request, responseObserver);
  }

  @Override
  public void update(
      UpdateNodeRequest request,
      StreamObserver<UpdateNodeResponse> response
  ) {
    String nodeId = request.getNodeId();
    if (nodeId != null && !nodeId.isEmpty()) {
      Node.Status targetStatus = request.getNewStatus();
      repository.updateStatus(nodeId, targetStatus);
    }
    response.onNext(UpdateNodeResponse.newBuilder().build());
    response.onCompleted();
  }

  @Override
  public void find(
      FindNodeRequest request,
      StreamObserver<FindNodeResponse> response
  ) {
    String nodeId = request.getNodeId();
    if (nodeId != null && !nodeId.isEmpty()) {
      repository.find(nodeId)
        .thenApply(this::foundNode)
        .thenAccept(response::onNext)
        .exceptionally(failure -> {
          response.onNext(notFound());
          return null;
        });
    } else {
      response.onNext(notFound());
    }
    response.onCompleted();
  }

  private FindNodeResponse notFound() {
    return FindNodeResponse.newBuilder().build();
  }

  private FindNodeResponse foundNode(Node node) {
    return FindNodeResponse.newBuilder().setNode(node).build();
  }

  @Override
  public void list(
      ListNodesRequest request,
      StreamObserver<ListNodesResponse> responseObserver
  ) {
    super.list(request, responseObserver);
  }
}