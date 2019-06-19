package net.twerion.armada.api.ship;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

import net.twerion.armada.Ship;
import net.twerion.armada.api.KeyPath;
import net.twerion.armada.api.redis.RedisObjectSchema;

public final class RedisShipRepository implements ShipRepository {
  private KeyPath path;
  private Executor executor;
  private RedisObjectSchema<Ship.Builder> schema;
  private RedisAdvancedClusterCommands<String, ByteBuffer> store;

  private RedisShipRepository(
      KeyPath path,
      Executor executor,
      RedisObjectSchema<Ship.Builder> schema,
      RedisAdvancedClusterCommands<String, ByteBuffer> store
  ) {
    this.path = path;
    this.executor = executor;
    this.schema = schema;
    this.store = store;
  }

  @Override
  public CompletableFuture<?> create(Ship ship) {
    CompletableFuture<?> future = new CompletableFuture<>();
    executor.execute(() -> createAndComplete(ship, future));
    return future;
  }

  private void createAndComplete(Ship ship, CompletableFuture<?> future) {
    createBlocking(ship);
    future.complete(null);
  }

  private void createBlocking(Ship ship) {
    KeyPath nodePath = path.subPath(ship.getId());
    schema.put(nodePath, ship.toBuilder(), store::hmset);
  }

  @Override
  public CompletableFuture<?> updateLifecycleStage(
    String shipId, Ship.LifecycleStage stage) {

    CompletableFuture<?> future = new CompletableFuture<>();
    executor.execute(() -> updateLifecycleStageAndComplete(shipId, stage, future));
    return future;
  }

  private void updateLifecycleStageAndComplete(
    String shipId, Ship.LifecycleStage stage, CompletableFuture<?> future) {

    updateLifecycleStageBlocking(shipId, stage);
    future.complete(null);
  }

  private void updateLifecycleStageBlocking(String shipId, Ship.LifecycleStage stage) {
    String nodeKey = path.subPath(shipId).subPath("lifecycle_stage").value();
    byte[] encoded = ShipRedisSchema.encodeLifecycleStage(stage);
    store.set(nodeKey, ByteBuffer.wrap(encoded));
  }
}
