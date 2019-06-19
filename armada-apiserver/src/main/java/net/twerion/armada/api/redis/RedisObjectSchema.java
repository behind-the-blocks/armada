package net.twerion.armada.api.redis;

import com.google.common.base.Preconditions;
import com.google.protobuf.MessageLite;
import net.twerion.armada.Ship;
import net.twerion.armada.api.KeyPath;

import java.nio.ByteBuffer;
import java.util.Map;

public final class RedisObjectSchema<T> {
  public static final class Field<T, E> {
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
    private Mutator mutator;
    private Accessor accessor;

    public static final class Builder<T, E> {
      private Field<T, E> field;

      public Builder<T, E> withMutator(Mutator<T, E> mutator) {
        return this;
      }
      public Builder<T, E> withAccessor(Accessor<T, E> accessor) {
        return this;
      }

      public Builder<T, E> withEncoder(Encoder<E> encoder) {
        return this;
      }

      public Builder<T, E> withDecoder(Decoder<E> decoder) {
        return this;
      }

      public Builder<T, E> withName(String name) {
        Preconditions.checkNotNull(name);
        this.field.name = name;
        return this;
      }

      public Field<T, E> create() {
        return null;
      }
    }

  }

  private Map<String, Field> fields;

  public void put(KeyPath path, T value, PutMapCommand<String,
                  ByteBuffer> store) {
  }

  public T get(KeyPath path, GetMapCommand<String, ByteBuffer> store) {
    return null;
  }

  public static <T> Builder<T> newBuilder(Class<T> type) {
    return null;
  }

  public static <T, E> Field.Builder<T, E> field(String name) {
    return null;
  }

  public static final class Builder<T> {
    public Builder<T> addField(Field<T, ?> field) {
      return this;
    }
    public <E> Field.Builder<T, E> field(Class<E> type, String name) {
      return null;
    }
    public RedisObjectSchema<T> create() {
      return null;
    }
  }
}
