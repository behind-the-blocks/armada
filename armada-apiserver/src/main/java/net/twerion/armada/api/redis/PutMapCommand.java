package net.twerion.armada.api.redis;

import java.util.Map;

public interface PutMapCommand<K, V> {
  void put(K path, Map<K, V> fields);
}
