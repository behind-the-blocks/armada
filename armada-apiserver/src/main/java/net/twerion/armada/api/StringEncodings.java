package net.twerion.armada.api;

import com.google.common.base.Charsets;

public final class StringEncodings {
  private StringEncodings() {}

  public static byte[] encodeUtf8(String value) {
    return value.getBytes(Charsets.UTF_8);
  }

  public static String decodeUtf8(byte[] buffer) {
    return new String(buffer, Charsets.UTF_8);
  }
}
