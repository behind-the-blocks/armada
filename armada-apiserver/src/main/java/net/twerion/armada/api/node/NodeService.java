package net.twerion.armada.api.node;

import javax.inject.Inject;

import io.grpc.stub.StreamObserver;

import net.twerion.armada.api.FindNodeByIdRequest;
import net.twerion.armada.api.FindNodeResponse;
import net.twerion.armada.api.ListNodesRequest;
import net.twerion.armada.api.ListNodesResponse;
import net.twerion.armada.api.ListNodesByLabelRequest;
import net.twerion.armada.api.CreateNodeRequest;
import net.twerion.armada.api.CreateNodeResponse;
import net.twerion.armada.api.DeleteNodeResponse;
import net.twerion.armada.api.DeleteNodeRequest;
import net.twerion.armada.api.NodeServiceGrpc;

public final class NodeService extends NodeServiceGrpc.NodeServiceImplBase {
  @Inject
  private NodeService() {}

  @Override
  public void findById(FindNodeByIdRequest request, StreamObserver<FindNodeResponse> responseObserver) {
    super.findById(request, responseObserver);
  }

  @Override
  public void list(ListNodesRequest request, StreamObserver<ListNodesResponse> responseObserver) {
    super.list(request, responseObserver);
  }

  @Override
  public void listByLabel(ListNodesByLabelRequest request, StreamObserver<ListNodesResponse> responseObserver) {
    super.listByLabel(request, responseObserver);
  }

  @Override
  public void create(CreateNodeRequest request, StreamObserver<CreateNodeResponse> responseObserver) {
    super.create(request, responseObserver);
  }

  @Override
  public void delete(DeleteNodeRequest request, StreamObserver<DeleteNodeResponse> responseObserver) {
    super.delete(request, responseObserver);
  }
}
