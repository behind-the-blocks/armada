// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.store;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import net.twerion.armada.advisor.LocalNodeDescriptor;

public final class ZookeeperResourceStoreFactory
    implements ResourceStoreFactory, Provider<ZookeeperResourceStore> {

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

  public ZookeeperResourceStore createResourceStore() {
    CuratorFramework curator = CuratorFrameworkFactory.newClient(
        config.connectionString(),
        config.retryPolicy()
    );
    return new ZookeeperResourceStore(
      curator,
      config,
      nodeDescriptor,
      fallbackExecutor
    );
  }

  @Override
  public ZookeeperResourceStore get() {
    return createResourceStore();
  }
}