package net.twerion.armada.api.node;

import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;

import java.nio.ByteBuffer;

public final class RedisNodeRepository {
  private RedisAdvancedClusterReactiveCommands<String, ByteBuffer> store;

  private RedisNodeRepository(
    RedisAdvancedClusterReactiveCommands<String, ByteBuffer> store
  ) {
    this.store = store;
  }



}