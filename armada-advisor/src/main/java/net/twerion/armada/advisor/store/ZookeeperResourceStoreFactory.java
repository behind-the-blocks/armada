package net.twerion.armada.advisor.store;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import net.twerion.armada.advisor.LocalNodeDescriptor;

public final class ZookeeperResourceStoreFactory {
  private String connectionString;
  private RetryPolicy retryPolicy;
  private Executor fallbackExecutor;
  private ZookeeperResourceStoreConfig config;
  private LocalNodeDescriptor nodeDescriptor;

  private ZookeeperResourceStoreFactory(
    String connectionString,
    RetryPolicy retryPolicy,
    Executor fallbackExecutor,
    LocalNodeDescriptor nodeDescriptor,
    ZookeeperResourceStoreConfig config
  ) {
    this.config = config;
    this.retryPolicy = retryPolicy;
    this.fallbackExecutor = fallbackExecutor;
    this.nodeDescriptor = nodeDescriptor;
    this.connectionString = connectionString;
  }

  public ZookeeperResourceStore createZookeeperResourceStore() {
    CuratorFramework curator = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
    Logger logger = LogManager.getLogger(ZookeeperResourceStore.class);

    return new ZookeeperResourceStore(
      logger,
      curator,
      config,
      nodeDescriptor,
      fallbackExecutor
    );
  }
}