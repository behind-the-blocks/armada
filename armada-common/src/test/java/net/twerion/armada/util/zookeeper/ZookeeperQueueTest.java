package net.twerion.armada.util.zookeeper;

import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.shaded.com.google.common.base.Charsets;


public final class ZookeeperQueueTest {
  private static final String PATH = "/test/queue";

  private TestingServer server;
  private CuratorFramework framework;

  @Before
  public void setupTestingServer() throws Exception {
    this.server = new TestingServer();
    this.server.start();
    this.framework = CuratorFrameworkFactory.newClient(
      server.getConnectString(), new RetryNTimes(1, 1));

    this.framework.start();
  }

  @After
  public void tearDownTestingServer() throws Exception {
    this.server.close();
    this.framework.close();
  }

  @Test
  public void testQueueInsertion() {
    ZookeeperQueue queue = ZookeeperQueue.create(PATH, framework);
    byte[] dummyData =createDummyData();

    boolean inserted = queue.tryInsert(dummyData);
    Assert.assertTrue(inserted);

    Optional<byte[]> head = queue.popHead();
    Assert.assertTrue(head.isPresent());
    Assert.assertArrayEquals(dummyData, head.get());
  }

  private byte[] createDummyData() {
    return "dummy".getBytes(Charsets.UTF_8);
  }
}
