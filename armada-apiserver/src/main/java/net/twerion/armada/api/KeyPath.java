package net.twerion.armada.api;

import io.etcd.jetcd.ByteSequence;

public interface KeyPath {
  KeyPath subPath(String value);
  String value();
  ByteSequence bytes();
}
