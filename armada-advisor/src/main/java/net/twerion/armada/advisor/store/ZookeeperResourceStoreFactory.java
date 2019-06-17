package net.twerion.armada.advisor.store;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

public final class ZookeeperResourceStoreFactory {
  private String connectionString;
  private RetryPolicy retryPolicy;
  private ZookeeperResourceStoreConfig config;

  private ZookeeperResourceStoreFactory(
    String connectionString,
    RetryPolicy retryPolicy,
    ZookeeperResourceStoreConfig config
  ) {
    this.connectionString = connectionString;
    this.retryPolicy = retryPolicy;
    this.config = config;
  }

  public ZookeeperResourceStore createZookeeperResourceStore() {
    CuratorFramework curator = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
    return new ZookeeperResourceStore(curator, config);
  }
}