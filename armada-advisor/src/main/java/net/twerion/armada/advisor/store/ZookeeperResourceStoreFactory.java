package net.twerion.armada.advisor.store;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import net.twerion.armada.advisor.LocalNodeDescriptor;

public final class ZookeeperResourceStoreFactory
  implements Provider<ZookeeperResourceStore> {

  private Executor fallbackExecutor;
  private ZookeeperResourceStoreConfig config;
  private LocalNodeDescriptor nodeDescriptor;

  @Inject
  private ZookeeperResourceStoreFactory(
    Executor fallbackExecutor,
    LocalNodeDescriptor nodeDescriptor,
    ZookeeperResourceStoreConfig config
  ) {
    this.config = config;
    this.fallbackExecutor = fallbackExecutor;
    this.nodeDescriptor = nodeDescriptor;
  }

  public ZookeeperResourceStore createZookeeperResourceStore() {
    Logger logger = LogManager.getLogger(ZookeeperResourceStore.class);
    CuratorFramework curator = CuratorFrameworkFactory.newClient(
        config.connectionString(), config.retryPolicy());

    return new ZookeeperResourceStore(
      logger,
      curator,
      config,
      nodeDescriptor,
      fallbackExecutor
    );
  }

  @Override
  public ZookeeperResourceStore get() {
    return createZookeeperResourceStore();
  }
}