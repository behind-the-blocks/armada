package net.twerion.armada.api.ship;

import com.google.common.base.MoreObjects;
import net.twerion.armada.Ship;
import net.twerion.armada.ShipBlueprint;
import net.twerion.armada.api.StringEncodings;
import net.twerion.armada.api.redis.RedisObjectSchema;

public final class ShipRedisSchema {
  private ShipRedisSchema() {
  }

  public RedisObjectSchema<Ship.Builder> createSchema() {
    RedisObjectSchema.Builder<Ship.Builder> builder =
      RedisObjectSchema.newBuilder(Ship.Builder.class);

    builder.addField(
      builder.field(String.class, "blueprint_id")
        .withMutator(Ship.Builder::setBlueprintId)
        .withAccessor(Ship.Builder::getBlueprintId)
        .withEncoder(StringEncodings::encodeUtf8)
        .withDecoder(StringEncodings::decodeUtf8)
        .create());

    builder.addField(
      builder.field(ShipBlueprint.class, "blueprint")
        .withMutator(Ship.Builder::setBlueprint)
        .withAccessor(Ship.Builder::getBlueprint)
        .withEncoder(ShipBlueprint::toByteArray)
        .withDecoder(ShipBlueprint::parseFrom)
        .create());

    builder.addField(
      builder.field(String.class, "node_id")
        .withMutator(Ship.Builder::setNodeId)
        .withAccessor(Ship.Builder::getNodeId)
        .withEncoder(StringEncodings::encodeUtf8)
        .withDecoder(StringEncodings::decodeUtf8)
        .create());

    builder.addField(
      builder.field(String.class, "custom_stage")
        .withMutator(Ship.Builder::setCustomStage)
        .withAccessor(Ship.Builder::getCustomStage)
        .withEncoder(StringEncodings::encodeUtf8)
        .withDecoder(StringEncodings::decodeUtf8)
        .create());

    builder.addField(
      builder.field(Ship.LifecycleStage.class, "lifecycle_stage")
        .withMutator(Ship.Builder::setLifecycleStage)
        .withAccessor(Ship.Builder::getLifecycleStage)
        .withEncoder(ShipRedisSchema::encodeLifecycleStage)
        .withDecoder(ShipRedisSchema::decodeLifecycleStage)
        .create());

    return builder.create();
  }

  static byte[] encodeLifecycleStage(Ship.LifecycleStage stage) {
    return new byte[]{(byte) stage.ordinal()};
  }

  static Ship.LifecycleStage decodeLifecycleStage(byte[] buffer) {
    if (buffer.length != 1) {
      return Ship.LifecycleStage.UNKNOWN;
    }

    Ship.LifecycleStage stage = Ship.LifecycleStage.valueOf(buffer[0]);
    return MoreObjects.firstNonNull(stage, Ship.LifecycleStage.UNKNOWN);
  }

  public static ShipRedisSchema create() {
    return new ShipRedisSchema();
  }
}