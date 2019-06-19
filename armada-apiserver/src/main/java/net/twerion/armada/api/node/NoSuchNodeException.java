package net.twerion.armada.api.node;

public final class NoSuchNodeException extends Exception {
  private NoSuchNodeException(String message) {
    super(message);
  }

  public static NoSuchNodeException withId(String nodeId) {
    return new NoSuchNodeException(String.format(
      "Could not find node with id '%s'", nodeId
    ));
  }
}
