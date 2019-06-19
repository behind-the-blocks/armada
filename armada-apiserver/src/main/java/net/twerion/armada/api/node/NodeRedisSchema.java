package net.twerion.armada.api.node;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;

import net.twerion.armada.Node;
import net.twerion.armada.LabelSet;
import net.twerion.armada.Address;
import net.twerion.armada.Resources;
import net.twerion.armada.LabelSelectorSet;
import net.twerion.armada.NodeAppStarterSet;
import net.twerion.armada.api.redis.RedisObjectSchema;

public final class NodeRedisSchema {
  private NodeRedisSchema() {
  }

  public RedisObjectSchema<Node.Builder> create() {
    RedisObjectSchema.Builder<Node.Builder> builder =
      RedisObjectSchema.newBuilder(Node.Builder.class);

    builder.addField(
      builder.field(String.class, "cluster_id")
        .withMutator(Node.Builder::setClusterId)
        .withAccessor(Node.Builder::getClusterId)
        .withEncoder(NodeRedisSchema::toUtf8)
        .withDecoder(NodeRedisSchema::fromUtf8)
        .create());

    builder.addField(
      builder.field(Node.Status.class, "status")
        .withMutator(Node.Builder::setStatus)
        .withAccessor(Node.Builder::getStatus)
        .withEncoder(NodeRedisSchema::encodeStatus)
        .withDecoder(NodeRedisSchema::decodeStatus)
        .create());

    builder.addField(
      builder.field(LabelSet.class, "labels")
        .withMutator(Node.Builder::setLabels)
        .withAccessor(Node.Builder::getLabels)
        .withEncoder(LabelSet::toByteArray)
        .withDecoder(LabelSet::parseFrom)
        .create());

    builder.addField(
      builder.field(Address.class, "address")
        .withMutator(Node.Builder::setAddress)
        .withAccessor(Node.Builder::getAddress)
        .withEncoder(Address::toByteArray)
        .withDecoder(Address::parseFrom)
        .create());

    builder.addField(
      builder.field(LabelSelectorSet.class, "ship_selectors")
        .withMutator(Node.Builder::setShipSelectors)
        .withAccessor(Node.Builder::getShipSelectors)
        .withEncoder(LabelSelectorSet::toByteArray)
        .withDecoder(LabelSelectorSet::parseFrom)
        .create());

    builder.addField(
      builder.field(Resources.class, "resources")
        .withMutator(Node.Builder::setResources)
        .withAccessor(Node.Builder::getResources)
        .withEncoder(Resources::toByteArray)
        .withDecoder(Resources::parseFrom)
        .create());

    builder.addField(
      builder.field(NodeAppStarterSet.class, "app_starters")
        .withMutator(Node.Builder::setAppStarters)
        .withAccessor(Node.Builder::getAppStarters)
        .withEncoder(NodeAppStarterSet::toByteArray)
        .withDecoder(NodeAppStarterSet::parseFrom)
        .create()
    );

    return builder.create();
  }

  private static Node.Status decodeStatus(byte[] encoded) {
    if (encoded.length != 1) {
      return Node.Status.UNKNOWN;
    }
    Node.Status decoded = Node.Status.valueOf(encoded[0]);
    return MoreObjects.firstNonNull(decoded, Node.Status.UNKNOWN);
  }

  private static byte[] encodeStatus(Node.Status status) {
    return new byte[]{(byte) status.ordinal()};
  }

  private static byte[] toUtf8(String value) {
    return value.getBytes(Charsets.UTF_8);
  }

  private static String fromUtf8(byte[] bytes) {
    return new String(bytes, Charsets.UTF_8);
  }
}
