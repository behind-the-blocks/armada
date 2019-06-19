package net.twerion.armada.scheduler.queue;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.protobuf.InvalidProtocolBufferException;

import net.twerion.armada.util.zookeeper.ZookeeperQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.Ship;

public final class ZookeeperShipQueue implements ShipQueue {
  private static final Logger LOG = LogManager.getLogger(ZookeeperShipQueue.class);

  private ZookeeperQueue queue;

  private ZookeeperShipQueue(ZookeeperQueue queue) {
    this.queue = queue;
  }

  @Override
  public void commitHead() {
    queue.removeHead();
  }

  @Override
  public boolean addLast(Ship element) {
    return queue.tryInsert(element.toByteArray());
  }

  @Override
  public Optional<Ship> head() {
    return queue.head().map(this::tryParseShip);
  }

  @Nullable
  private Ship tryParseShip(byte[] bytes) {
    try {
      return Ship.parseFrom(bytes);
    } catch (InvalidProtocolBufferException illegalFormat) {
      LOG.error("Invalid bytes in queue", illegalFormat);
      return null;
    }
  }
}
