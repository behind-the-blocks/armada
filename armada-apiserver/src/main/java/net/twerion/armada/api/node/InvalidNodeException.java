package net.twerion.armada.api.node;

public final class InvalidNodeException extends Exception {
  private InvalidNodeException(String message) {
    super(message);
  }

  public static InvalidNodeException missingField(String field) {
    return new InvalidNodeException(String.format(
      "Could not find required field %s", field
    ));
  }
}
