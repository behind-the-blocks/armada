package net.twerion.armada.api.node;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.base.Charsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.etcd.jetcd.KV;
import io.etcd.jetcd.ByteSequence;

import net.twerion.armada.Node;
import net.twerion.armada.api.KeyPath;

import static net.twerion.armada.api.node.EtcdNodePaths.clusterIdPath;
import static net.twerion.armada.api.node.EtcdNodePaths.labelsPath;
import static net.twerion.armada.api.node.EtcdNodePaths.shipSelectorPath;
import static net.twerion.armada.api.node.EtcdNodePaths.statusPath;

// Currently no transactions are used
@SuppressWarnings("UnstableApiUsage")
public final class EtcdNodeRepository implements NodeRepository {
  private static final Logger LOG = LogManager.getLogger(EtcdNodeRepository.class);

  private KV store;
  private Executor executor;
  private KeyPath basePath;

  private EtcdNodeRepository(KV store, Executor executor, KeyPath basePath) {
    this.store = store;
    this.executor = executor;
    this.basePath = basePath;
  }

  @Override
  public CompletableFuture<?> create(Node node) {
    CompletableFuture<?> future = new CompletableFuture<>();
    executor.execute(() -> tryCreateBlockingAndComplete(node, future));
    return future;
  }

  private void tryCreateBlockingAndComplete(Node node, CompletableFuture<?> future) {
    try {
      tryCreateBlocking(node);
      future.complete(null);
    } catch (Exception creationFailure) {
      future.completeExceptionally(creationFailure);
    }
  }

  private void tryCreateBlocking(Node node) throws Exception {
    KeyPath path = basePath.subPath(node.getId());
    byte[] encodedStatus = { (byte) node.getStatusValue() };
    byte[] shipSelector = node.getShipSelectors().toByteArray();
    byte[] labels = node.getLabels().toByteArray();
    ByteSequence clusterId = ByteSequence.from(node.getClusterId(), Charsets.UTF_8);
    store.put(labelsPath(path), bytes(labels));
    store.put(clusterIdPath(path), clusterId);
    store.put(statusPath(path), bytes(encodedStatus));
    store.put(shipSelectorPath(path), bytes(shipSelector));
  }

  @Override
  public CompletableFuture<Node> find(String nodeId) {
    KeyPath path = basePath.subPath(nodeId);
    CompletableFuture<Node> future = new CompletableFuture<>();
    executor.execute(() -> readNodeAndComplete(path, future));
    return future;
  }

  private void readNodeAndComplete(KeyPath path, CompletableFuture<Node> future) {
  }

  @Override
  public CompletableFuture<?> updateStatus(String nodeId, Node.Status status) {
    CompletableFuture<?> future = new CompletableFuture<>();
    tryUpdateStatusAndComplete(nodeId, status, future);
    return future;
  }

  private void tryUpdateStatusAndComplete(
    String nodeId, Node.Status status, CompletableFuture<?> future) {

    try {
      tryUpdateStatusBlocking(nodeId, status);
      future.complete(null);
    } catch (Exception updateFailure) {
      future.completeExceptionally(updateFailure);
    }
  }

  private void tryUpdateStatusBlocking(String nodeId, Node.Status status) {
    KeyPath path = basePath.subPath(nodeId);
    byte[] encodedStatus = { (byte) status.ordinal() };
    store.put(statusPath(path), bytes(encodedStatus));
  }

  @Override
  public CompletableFuture<?> delete(String nodeId) {
    return null;
  }

  private static ByteSequence bytes(byte[] bytes) {
    return ByteSequence.from(bytes);
  }
}
