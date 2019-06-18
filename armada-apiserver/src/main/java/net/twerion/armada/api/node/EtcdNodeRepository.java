package net.twerion.armada.api.node;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;

import com.google.protobuf.InvalidProtocolBufferException;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.ByteSequence;

import net.twerion.armada.LabelSelectorSet;
import net.twerion.armada.LabelSet;
import net.twerion.armada.Node;
import net.twerion.armada.api.KeyPath;
import net.twerion.armada.api.etcd.KeyValues;

// Currently no transactions are used
@SuppressWarnings("UnstableApiUsage")
public final class EtcdNodeRepository implements NodeRepository {
  private KV store;
  private Executor executor;
  private KeyPath basePath;

  private EtcdNodeRepository() {}

  private ByteSequence clusterIdPath(KeyPath base) {
    return base.subPath("cluster_id").bytes();
  }

  private ByteSequence statusPath(KeyPath base) {
    return base.subPath("status").bytes();
  }

  private ByteSequence labelsPath(KeyPath base) {
    return base.subPath("labels").bytes();
  }

  private ByteSequence shipSelectorPath(KeyPath base) {
    return base.subPath("ship_selector").bytes();
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
    executor.execute(() -> readNode(path, future));
    return future;
  }

  @Nullable
  private GetResponse tryGetNodeFields(KeyPath path, Consumer<Exception> failer) {
    try {
      return store.get(basePath.bytes()).get();
    } catch (Exception failure) {
      failer.accept(failure);
    }
    return null;
  }

  private void readNode(KeyPath path, CompletableFuture<Node> future) {
    GetResponse response = tryGetNodeFields(path, future::completeExceptionally);
    if (response == null) {
      return;
    }

    Node.Builder node = Node.newBuilder();
    Map<ByteSequence, ByteSequence> entries = KeyValues.toMap(response.getKvs());

    ByteSequence clusterId = entries.get(clusterIdPath(path));
    if (clusterId == null) {
      future.completeExceptionally(InvalidNodeException.missingField("clusterId"));
      return;
    }

    node.setClusterId(clusterId.toString(Charsets.UTF_8));
    ByteSequence status = entries.get(statusPath(path));
    if (status == null) {
      node.setStatus(Node.Status.OFFLINE);
    } else {
      byte[] statusBytes = status.getBytes();
      if (statusBytes.length == 1) {
        node.setStatusValue(status.getBytes()[0]);
      } else {
        node.setStatus(Node.Status.OFFLINE);
      }
    }

    ByteSequence shipSelector = entries.get(shipSelectorPath(path));
    if (shipSelector == null) {
      node.setShipSelectors(LabelSelectorSet.newBuilder().build());
    } else {
      LabelSelectorSet decoded = tryDecodeLabelSelectorSet(shipSelector);
      if (decoded == null) {
        node.setShipSelectors(LabelSelectorSet.newBuilder().build());
      } else {
        node.setShipSelectors(decoded);
      }
    }

    ByteSequence labels = entries.get(labelsPath(path));
    if (labels == null) {
      node.setLabels(LabelSet.newBuilder());
    } else {
      LabelSet decoded = tryDecodeLabelSet(labels);
      if (decoded == null) {
        node.setLabels(LabelSet.newBuilder().build());
      } else {
        node.setLabels(decoded);
      }
    }

    future.complete(node.build());
  }

  @Nullable
  private LabelSet tryDecodeLabelSet(ByteSequence bytes) {
    try {
      return LabelSet.parseFrom(bytes.getBytes());
    } catch (InvalidProtocolBufferException invalidProtocolBuffer) {
      return null;
    }
  }

  @Nullable
  private LabelSelectorSet tryDecodeLabelSelectorSet(ByteSequence bytes) {
    try {
      return LabelSelectorSet.parseFrom(bytes.getBytes());
    } catch (InvalidProtocolBufferException invalidProtocolBuffer) {
      return null;
    }
  }


  @Override
  public CompletableFuture<?> updateStatus(String nodeId, Node.Status status) {
    CompletableFuture<?> future = new CompletableFuture<>();

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
    String nodePath = pathFormat.createSubPath(basePath, nodeId);

  }

  @Override
  public void delete(String nodeId) {
    try {
      store.delete(subPath(basePath, nodeId));
    } catch (Exception deletionFailure) {

    }
  }

  private ByteSequence bytes(byte[] bytes) {
    return ByteSequence.from(bytes);
  }
}
