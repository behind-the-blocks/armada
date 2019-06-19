package net.twerion.armada.api.redis;

import com.google.common.base.Charsets;
import net.twerion.armada.Address;
import net.twerion.armada.api.DelimitedKeyPath;
import net.twerion.armada.api.KeyPath;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class RedisObjectSchemaTest {
  public static final class MockStore<K, V>
    implements GetMapCommand<K, V>, PutMapCommand<K, V> {

    private Map<K, Map<K, V>> store;

    private MockStore(Map<K, Map<K, V>> store) {
      this.store = store;
    }

    @Override
    public String put(K path, Map<K, V> fields) {
      store.put(path, fields);
      return "";
    }

    @Override
    public Map<K, V> get(K path) {
      return store.get(path);
    }

    static <K, V> MockStore<K, V> create() {
      return new MockStore<>(new HashMap<>());
    }
  }

  @Test
  public void testStoreAndReadConsistency() {
    MockStore<String, ByteBuffer> store = MockStore.create();
    RedisObjectSchema<Address.Builder> schema = createAddressScheme();
    Address.Builder addressBuilder = Address.newBuilder()
      .setIpAddress("localhost")
      .setPort(1337);

    KeyPath path = DelimitedKeyPath.create(":", "test").subPath("a");
    schema.put(path, addressBuilder, store);

    Address.Builder read = schema.get(path, store);
    Assert.assertEquals(addressBuilder.build(), read.build());
  }

  public RedisObjectSchema<Address.Builder> createAddressScheme() {
    RedisObjectSchema.Builder<Address.Builder> builder =
      RedisObjectSchema.newBuilder(Address.Builder.class)
        .withFactory(Address::newBuilder);

    builder.addField(builder.field(String.class, "ip_address")
      .withMutator(Address.Builder::setIpAddress)
      .withAccessor(Address.Builder::getIpAddress)
      .withEncoder(RedisObjectSchemaTest::encodeUtf8)
      .withDecoder(RedisObjectSchemaTest::decodeUtf8)
      .create());

    builder.addField(builder.field(int.class, "port")
      .withMutator(Address.Builder::setPort)
      .withAccessor(Address.Builder::getPort)
      .withEncoder(RedisObjectSchemaTest::encodePort)
      .withDecoder(RedisObjectSchemaTest::decodePort)
      .create());

    return builder.create();
  }

  private static byte[] encodePort(int port) {
    ByteBuffer buffer = ByteBuffer.wrap(new byte[2]);
    buffer.putShort((short) port);
    return buffer.array();
  }

  public static int decodePort(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    return buffer.getShort();
  }

  private static String decodeUtf8(byte[] bytes) {
    return new String(bytes, Charsets.UTF_8);
  }

  public static byte[] encodeUtf8(String string) {
    return string.getBytes(Charsets.UTF_8);
  }
}