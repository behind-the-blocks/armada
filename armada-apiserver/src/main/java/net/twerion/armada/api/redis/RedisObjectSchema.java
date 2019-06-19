package net.twerion.armada.api.redis;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import net.twerion.armada.api.KeyPath;

public final class RedisObjectSchema<T> {
  private Class<T> type;
  private Supplier<T> factory;
  private Map<String, RedisObjectSchemaField<T, ?>> fields;

  private RedisObjectSchema(
      Class<T> type,
      Supplier<T> factory,
      Map<String, RedisObjectSchemaField<T, ?>> fields
  ) {
    this.type = type;
    this.factory = factory;
    this.fields = fields;
  }

  public void put(
    KeyPath path, T value, PutMapCommand<String, ByteBuffer> store) {

    Map<String, ByteBuffer> contents = new HashMap<>();
    for (RedisObjectSchemaField<T, ?> field : fields.values()) {
      Object fieldValue = field.accessor().access(value);
      byte[] encoded = field.wildcardEncode(fieldValue);
      contents.put(field.name(), ByteBuffer.wrap(encoded));
    }
    store.put(path.value(), contents);
  }

  public T get(KeyPath path, GetMapCommand<String, ByteBuffer> store) {
    String pathName = path.value();
    Map<String, ByteBuffer> contents = store.get(pathName);
    T target  = factory.get();

    for (RedisObjectSchemaField<T, ?> field : fields.values()) {
      byte[] encodedFieldValue = contents.get(field.name()).array();
      field.decodeAndSet(pathName, encodedFieldValue, target);
    }
    return target;
  }

  public static <T> Builder<T> newBuilder(Class<T> type) {
    return new Builder<>(type, null, new HashMap<>());
  }

  public static final class Builder<T> {
    @Nullable
    private Supplier<T> factory;
    private Class<T> type;
    private Map<String, RedisObjectSchemaField<T, ?>> fields;

    private Builder(
        Class<T> type,
        @Nullable  Supplier<T> factory,
        Map<String, RedisObjectSchemaField<T, ?>> fields
    ) {
      this.type = type;
      this.factory = factory;
      this.fields = fields;
    }

    public Builder<T> withFactory(Supplier<T> factory) {
      Preconditions.checkNotNull(factory);
      this.factory = factory;
      return this;
    }

    public Builder<T> withType(Class<T> type) {
      Preconditions.checkNotNull(type);
      this.type = type;
      return this;
    }

    public Builder<T> addField(RedisObjectSchemaField<T, ?> field) {
      Preconditions.checkNotNull(field);
      fields.put(field.name(), field);
      return this;
    }

    public <E> RedisObjectSchemaField.Builder<T, E> field(Class<E> type, String name) {
      return RedisObjectSchemaField.newBuilder(type, name);
    }

    public RedisObjectSchema<T> create() {
      Preconditions.checkNotNull(factory);
      return new RedisObjectSchema<>(
        type, factory, ImmutableMap.copyOf(fields)
      );
    }
  }
}
