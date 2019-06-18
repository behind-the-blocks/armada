package net.twerion.armada.api.etcd;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KeyValue;

public final class KeyValues {
  private KeyValues() {}

  public static Map<ByteSequence, ByteSequence> toMap(Collection<KeyValue> pairs) {
    Map<ByteSequence, ByteSequence> map = Maps.newHashMapWithExpectedSize(pairs.size());
    for (KeyValue pair : pairs) {
      map.put(pair.getKey(), pair.getValue());
    }
    return map;
  }
}
