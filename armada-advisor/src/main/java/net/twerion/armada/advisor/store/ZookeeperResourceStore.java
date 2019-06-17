package net.twerion.armada.advisor.store;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

import org.apache.curator.framework.CuratorFramework;

import net.twerion.armada.Resources;
import net.twerion.armada.advisor.LocalNodeDescriptor;

public final class ZookeeperResourceStore implements ResourceStore {
  private Logger logger;
  private CuratorFramework curator;
  private ZookeeperResourceStoreConfig config;
  private LocalNodeDescriptor nodeDescriptor;
  private Executor fallbackExecutor;

  ZookeeperResourceStore(
      Logger logger,
      CuratorFramework curator,
      ZookeeperResourceStoreConfig config,
      LocalNodeDescriptor nodeDescriptor,
      Executor fallbackExecutor
  ) {
    this.logger = logger;
    this.config = config;
    this.curator = curator;
    this.nodeDescriptor = nodeDescriptor;
    this.fallbackExecutor = fallbackExecutor;
  }

  @Override
  public void store(Resources resources) {
    String path = String.format("%s/%s", config.basePath(), nodeDescriptor.id());
    byte[] encodedResources = resources.toByteArray();
    try {
      curator.create()
        .orSetData()
        .withTtl(config.timeToLife())
        .creatingParentContainersIfNeeded()
        .forPath(path, encodedResources);
    } catch (Exception creationFailure) {
      String errorMessage = String.format("Failed writing resource to path %s", path);
      logger.error(errorMessage, creationFailure);
    }
  }

  @Override
  public CompletableFuture<?> storeAsync(Resources resources, Executor executor) {
    CompletableFuture<?> completer = new CompletableFuture<>();
    Runnable createAndComplete = () -> {
      store(resources);
      completer.complete(null);
    };

    executor.execute(createAndComplete);
    return completer;
  }

  @Override
  public CompletableFuture<?> storeAsync(Resources resources) {
    return storeAsync(resources,  fallbackExecutor);
  }
}
