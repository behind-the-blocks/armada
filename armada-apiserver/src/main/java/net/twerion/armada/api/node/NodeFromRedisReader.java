package net.twerion.armada.api.node;



import java.nio.ByteBuffer;

import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import net.twerion.armada.Node;
import net.twerion.armada.api.KeyPath;

public final class NodeFromRedisReader {
  private KeyPath path;
  private Node.Builder nodeBuilder;
  private RedisAdvancedClusterCommands<String, ByteBuffer> store;

  private NodeFromRedisReader(
      KeyPath path,
      Node.Builder nodeBuilder,
      RedisAdvancedClusterCommands<String, ByteBuffer> store
  ) {
    this.path = path;
    this.nodeBuilder = nodeBuilder;
    this.store = store;
  }

  public Node read() {
    return nodeBuilder.build();
  }
}
