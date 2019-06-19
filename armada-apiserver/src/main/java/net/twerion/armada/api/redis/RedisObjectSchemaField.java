package net.twerion.armada.api.redis;

import com.google.common.base.Preconditions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RedisObjectSchemaField<T, E> {
  private static final Logger LOG = LogManager.getLogger(RedisObjectSchemaField.class);

  public interface Mutator<T, E> {
    void mutate(T type, E value);
  }

  public interface Accessor<T, E> {
    E access(T type);
  }

  public interface Encoder<E> {
    byte[] toBytes(E value);
  }

  public interface Decoder<E> {
    E fromBytes(byte[] bytes) throws Exception;
  }

  private String name;
  private Class<E> type;
  private Mutator<T, E> mutator;
  private Accessor<T, E> accessor;
  private Encoder<E> encoder;
  private Decoder<E> decoder;

  private RedisObjectSchemaField() {} // For prototype creation

  private RedisObjectSchemaField(
      Class<E> type,
      String name,
      Mutator<T, E> mutator,
      Accessor<T, E> accessor,
      Encoder<E> encoder,
      Decoder<E> decoder
  )  {
    this.type = type;
    this.name = name;
    this.mutator = mutator;
    this.accessor = accessor;
    this.encoder = encoder;
    this.decoder = decoder;
  }

  byte[] wildcardEncode(Object object) {
    return encoder.toBytes((E) object);
  }

  void decodeAndSet(String path, byte[] bytes, T value) {
    try {
      E fieldValue = decoder.fromBytes(bytes);
      mutator.mutate(value, fieldValue);
    } catch (Exception decoderFailure) {
      String errorMessage = String.format(
        "Failed decoding field of type %s at path %s", type.getName(), path
      );
      LOG.warn(errorMessage, decoderFailure);
    }

  }

  public Class<E> type() {
    return this.type;
  }

  public String name() {
    return this.name;
  }

  public Mutator<T, E> mutator() {
    return this.mutator;
  }

  public Accessor<T, E> accessor() {
    return this.accessor;
  }

  public Encoder<E> encoder() {
    return this.encoder;
  }

  public Decoder<E> decoder() {
    return this.decoder;
  }

  public static <T, E> Builder<T, E> newBuilder(Class<E> type, String name) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(name);
    return new Builder<T, E>(new RedisObjectSchemaField<>())
      .withType(type)
      .withName(name);
  }

  public static final class Builder<T, E> {
    private RedisObjectSchemaField<T, E> prototype;

    private Builder(RedisObjectSchemaField<T, E> prototype) {
      this.prototype = prototype;
    }

    public Builder<T, E> withType(Class<E> type) {
      Preconditions.checkNotNull(type);
      this.prototype.type = type;
      return this;
    }

    public Builder<T, E> withMutator(Mutator<T, E> mutator) {
      Preconditions.checkNotNull(mutator);
      this.prototype.mutator = mutator;
      return this;
    }

    public Builder<T, E> withAccessor(Accessor<T, E> accessor) {
      Preconditions.checkNotNull(accessor);
      this.prototype.accessor = accessor;
      return this;
    }

    public Builder<T, E> withEncoder(Encoder<E> encoder) {
      Preconditions.checkNotNull(encoder);
      this.prototype.encoder = encoder;
      return this;
    }

    public Builder<T, E> withDecoder(Decoder<E> decoder) {
      Preconditions.checkNotNull(decoder);
      this.prototype.decoder = decoder;
      return this;
    }

    public Builder<T, E> withName(String name) {
      Preconditions.checkNotNull(name);
      this.prototype.name = name;
      return this;
    }

    public RedisObjectSchemaField<T, E> create() {
      return new RedisObjectSchemaField<>(
        prototype.type,
        prototype.name,
        prototype.mutator,
        prototype.accessor,
        prototype.encoder,
        prototype.decoder
      );
    }
  }
}
