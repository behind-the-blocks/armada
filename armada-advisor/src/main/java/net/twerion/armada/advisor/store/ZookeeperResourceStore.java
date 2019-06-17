package net.twerion.armada.advisor.store;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.curator.framework.CuratorFramework;

import net.twerion.armada.Resources;

public class ZookeeperResourceStore implements ResourceStore {
  private CuratorFramework curator;
  private ZookeeperResourceStoreConfig config;

  ZookeeperResourceStore(
    CuratorFramework curator, ZookeeperResourceStoreConfig config) {

    this.curator = curator;
    this.config = config;
  }

  @Override
  public void store(Resources resources) {
    curator.
  }

  @Override
  public CompletableFuture<?> storeAsync(Resources resources) {
    return null;
  }

  @Override
  public CompletableFuture<?> storeAsync(Resources resources, Executor executor) {
    return null;
  }
}
