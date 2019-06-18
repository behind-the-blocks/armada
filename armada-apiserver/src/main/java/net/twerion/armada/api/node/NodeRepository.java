package net.twerion.armada.api.node;

import net.twerion.armada.Node;

import java.util.concurrent.CompletableFuture;

public interface NodeRepository {
  CompletableFuture<?> create(Node node);

  CompletableFuture<?> delete(String nodeId);

  CompletableFuture<?> updateStatus(String nodeId, Node.Status status);

  CompletableFuture<Node> find(String nodeId);
}
