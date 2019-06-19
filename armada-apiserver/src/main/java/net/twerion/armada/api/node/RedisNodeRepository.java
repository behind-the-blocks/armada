package net.twerion.armada.api.node;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

import net.twerion.armada.Node;
import net.twerion.armada.api.KeyPath;
import net.twerion.armada.api.redis.RedisObjectSchema;

import javax.annotation.Nullable;

public final class RedisNodeRepository implements NodeRepository {
  private KeyPath path;
  private RedisObjectSchema<Node.Builder> schema;
  private RedisAdvancedClusterCommands<String, ByteBuffer> store;
  private Executor executor;

  private RedisNodeRepository(
    KeyPath path,
    Executor executor,
    RedisObjectSchema<Node.Builder> schema,
    RedisAdvancedClusterCommands<String, ByteBuffer> store
  ) {
    this.path = path;
    this.store = store;
    this.schema = schema;
    this.executor = executor;
  }

  @Override
  public CompletableFuture<?> create(Node node) {
    CompletableFuture<?> future = new CompletableFuture<>();
    executor.execute(() ->createAndComplete(node, future));
    return future;
  }

  private void createAndComplete(Node node, CompletableFuture<?> future) {
    createBlocking(node);
    future.complete(null);
  }

  private void createBlocking(Node node) {
    KeyPath path = this.path.subPath(node.getId());
    schema.put(path, node.toBuilder(), store::hmset);
  }

  @Override
  public CompletableFuture<?> delete(String nodeId) {
    CompletableFuture<?> future = new CompletableFuture<>();
    executor.execute(() -> deleteAndComplete(nodeId, future));
    return future;
  }

  private void deleteAndComplete(String nodeId, CompletableFuture<?> future) {
    deleteBlocking(nodeId);
    future.complete(null);
  }

  private void deleteBlocking(String nodeId) {
    store.del(path.subPath(nodeId).value());
  }

  @Override
  public CompletableFuture<?> updateStatus(String nodeId, Node.Status status) {
    CompletableFuture<?> future = new CompletableFuture<>();
    executor.execute( () -> updateStatusAndComplete(nodeId, status, future));
    return future;
  }

  private void updateStatusAndComplete(
    String nodeId, Node.Status status, CompletableFuture<?> future) {

    updateStatusBlockign(nodeId, status);
    future.complete(null);
  }

  private void updateStatusBlockign(String nodeId, Node.Status status) {
    String key = this.path.subPath(nodeId).value();
    byte[] encodedStatus = { (byte) status.ordinal() };
    store.hset(key, "status", ByteBuffer.wrap(encodedStatus));
  }

  @Override
  public CompletableFuture<Node> find(String nodeId) {
    CompletableFuture<Node> future = new CompletableFuture<>();
    executor.execute(() -> findAndComplete(nodeId, future));
    return future;
  }

  private void findAndComplete(String nodeId, CompletableFuture<Node> future) {
    Node node = findBlocking(nodeId);
    if (node == null) {
      future.completeExceptionally(NoSuchNodeException.withId(nodeId));
      return;
    }
    future.complete(node);
  }

  @Nullable
  private Node findBlocking(String nodeId) {
    KeyPath path = this.path.subPath(nodeId);
    Node.Builder nodeBuilder = schema.get(path, store::hgetall);
    if ((nodeBuilder.getClusterId() == null)
      || (nodeBuilder.getClusterId().isEmpty())) {
      return null;
    }
    return nodeBuilder.build();
  }
}