package net.twerion.armada.api;

import com.google.common.base.Preconditions;

public class DelimitedKeyPath implements KeyPath {
  private String delimiter;
  private String path;

  private DelimitedKeyPath(String delimiter, String path) {
    this.delimiter = delimiter;
    this.path = path;
  }

  @Override
  public KeyPath subPath(String value) {
    String joined = String.join(path, delimiter, value);
    return DelimitedKeyPath.create(delimiter, joined);
  }

  @Override
  public String value() {
    return path;
  }

  public static DelimitedKeyPath withDelimiter(String delimiter) {
    return create(delimiter, "");
  }

  public static DelimitedKeyPath create(String delimiter, String path) {
    Preconditions.checkNotNull(delimiter);
    Preconditions.checkNotNull(path);
    return new DelimitedKeyPath(delimiter, path);
  }
}
