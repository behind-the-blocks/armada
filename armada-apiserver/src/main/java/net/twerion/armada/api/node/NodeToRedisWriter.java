package net.twerion.armada.api.node;

import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import net.twerion.armada.Node;

import java.nio.ByteBuffer;

public class NodeToRedisWriter {
  private Node node;
  private RedisAdvancedClusterReactiveCommands<String, ByteBuffer> store;

  private NodeToRedisWriter(
    Node node,
    RedisAdvancedClusterReactiveCommands<String, ByteBuffer> store
  ) {
    this.node = node;
    this.store = store;
  }

  public void write() {

  }
}
