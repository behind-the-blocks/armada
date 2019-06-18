package net.twerion.armada.api.node;

import io.etcd.jetcd.ByteSequence;

import net.twerion.armada.api.KeyPath;

// TODO(merlinosayimwen): Find an elegant way after the first major release
//  is done. The current way works and was done because translating pb into
//  a set of key, value pairs is very hard and it is time consuming to build
//  some generic solution for it.
final class EtcdNodePaths {
  private EtcdNodePaths() {}

  static ByteSequence clusterIdPath(KeyPath base) {
    return base.subPath("cluster_id").bytes();
  }

  static ByteSequence statusPath(KeyPath base) {
    return base.subPath("status").bytes();
  }

  static ByteSequence labelsPath(KeyPath base) {
    return base.subPath("labels").bytes();
  }

  static ByteSequence shipSelectorPath(KeyPath base) {
    return base.subPath("ship_selector").bytes();
  }
}
