package net.twerion.armada.api;

public interface KeyPath {
  KeyPath subPath(String value);
  String value();
}
