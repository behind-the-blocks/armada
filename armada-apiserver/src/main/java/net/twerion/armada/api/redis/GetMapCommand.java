package net.twerion.armada.api.redis;

import java.util.Map;

public interface GetMapCommand<K,V> {

  Map<K, V> get(K path);
}
