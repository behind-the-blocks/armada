package net.twerion.armada.api.redis;

import java.util.Map;

public interface PutMapCommand<K, V> {
  String put(K path, Map<K, V> fields);
}
