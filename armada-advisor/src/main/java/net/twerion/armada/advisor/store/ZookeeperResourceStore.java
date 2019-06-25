// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.store;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.curator.framework.CuratorFramework;

import net.twerion.armada.Resources;
import net.twerion.armada.advisor.LocalNodeDescriptor;

/**
 * Resource store implementation that stores resources into zookeeper.
 *
 * @see ResourceStore
 * @see ZookeeperResourceStoreConfig
 * @see ZookeeperResourceStoreFactory
 */
public final class ZookeeperResourceStore implements ResourceStore {
  private static Logger LOG = LogManager.getLogger(ZookeeperResourceStore.class);

  private CuratorFramework curator;
  private ZookeeperResourceStoreConfig config;
  private LocalNodeDescriptor nodeDescriptor;
  private Executor fallbackExecutor;

  ZookeeperResourceStore(
      CuratorFramework curator,
      ZookeeperResourceStoreConfig config,
      LocalNodeDescriptor nodeDescriptor,
      Executor fallbackExecutor
  ) {
    this.config = config;
    this.curator = curator;
    this.nodeDescriptor = nodeDescriptor;
    this.fallbackExecutor = fallbackExecutor;
  }

  @Override
  public void store(Resources resources) {
    String path = path();
    byte[] encodedResources = resources.toByteArray();
    try {
      curator.create()
        .orSetData()
        .withTtl(config.timeToLife())
        .creatingParentContainersIfNeeded()
        .forPath(path, encodedResources);
    } catch (Exception creationFailure) {
      String errorMessage = String.format("Failed writing resource to path %s", path);
      LOG.error(errorMessage, creationFailure);
    }
  }

  private String path() {
    return config.pathFormat().replace(
      ZookeeperResourceStoreConfig.PATH_NODE_ID_PLACEHOLDER,
      nodeDescriptor.id()
    );
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