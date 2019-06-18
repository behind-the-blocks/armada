package net.twerion.armada.api.node;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.protobuf.InvalidProtocolBufferException;

import io.etcd.jetcd.ByteSequence;

import net.twerion.armada.LabelSelectorSet;
import net.twerion.armada.LabelSet;
import net.twerion.armada.Node;
import net.twerion.armada.api.KeyPath;

import static net.twerion.armada.api.node.EtcdNodePaths.clusterIdPath;
import static net.twerion.armada.api.node.EtcdNodePaths.labelsPath;
import static net.twerion.armada.api.node.EtcdNodePaths.shipSelectorPath;
import static net.twerion.armada.api.node.EtcdNodePaths.statusPath;

public final class EtcdNodeReader {
  private KeyPath path;
  private Node.Builder nodeBuilder;
  private Map<ByteSequence, ByteSequence> entries;

  private EtcdNodeReader(
      KeyPath path,
      Node.Builder nodeBuilder,
      Map<ByteSequence, ByteSequence> entries
  ) {
    this.path = path;
    this.entries = entries;
    this.nodeBuilder = nodeBuilder;
  }

  private Node readNode() throws InvalidNodeException {
    // The clusterId is required. It verifies that the node actually exists.
    ByteSequence clusterId = entries.get(clusterIdPath(path));
    if (clusterId == null) {
      throw InvalidNodeException.missingField("clusterId");
    }

    decodeAndSetStatus(entries.get(statusPath(path)), nodeBuilder);
    decodeAndSetLabels(entries.get(labelsPath(path)), nodeBuilder);
    decodeAndSetShipSelector(entries.get(shipSelectorPath(path)), nodeBuilder);
    return nodeBuilder.build();
  }

  private static void decodeAndSetStatus(
    @Nullable ByteSequence status, Node.Builder nodeBuilder) {

    if (status == null) {
      nodeBuilder.setStatus(Node.Status.OFFLINE);
      return;
    }
    byte[] statusBytes = status.getBytes();
    if (statusBytes.length == 1) {
      nodeBuilder.setStatusValue(status.getBytes()[0]);
      return;
    }
    nodeBuilder.setStatus(Node.Status.OFFLINE);
  }

  private static void decodeAndSetLabels(
    @Nullable ByteSequence labels, Node.Builder nodeBuilder) {

    if (labels == null) {
      nodeBuilder.setLabels(LabelSet.newBuilder().build());
      return;
    }
    LabelSet decoded = tryDecodeLabelSet(labels);
    nodeBuilder.setLabels(decoded != null
      ? decoded
      : LabelSet.newBuilder().build()
    );
  }

  private static void decodeAndSetShipSelector(
    @Nullable ByteSequence shipSelector, Node.Builder nodeBuilder) {

    if (shipSelector == null) {
      nodeBuilder.setShipSelectors(LabelSelectorSet.newBuilder().build());
      return;
    }
    LabelSelectorSet decoded = tryDecodeLabelSelectorSet(shipSelector);
    nodeBuilder.setShipSelectors(decoded != null
      ? decoded
      : LabelSelectorSet.newBuilder().build()
    );
  }


  @Nullable
  private static LabelSet tryDecodeLabelSet(ByteSequence bytes) {
    try {
      return LabelSet.parseFrom(bytes.getBytes());
    } catch (InvalidProtocolBufferException invalidProtocolBuffer) {
      return null;
    }
  }

  @Nullable
  private static LabelSelectorSet tryDecodeLabelSelectorSet(ByteSequence bytes) {
    try {
      return LabelSelectorSet.parseFrom(bytes.getBytes());
    } catch (InvalidProtocolBufferException invalidProtocolBuffer) {
      return null;
    }
  }
}
