package net.twerion.armada.advisor.store;

import com.google.common.base.Preconditions;

public final class ZookeeperResourceStoreConfig {
  private long timeToLife;
  private String basePath;

  private ZookeeperResourceStoreConfig(long timeToLife, String basePath) {
    this.timeToLife = timeToLife;
  }

  public long timeToLife() {
    return timeToLife;
  }

  public String basePath() {
    return basePath;
  }

  public static Builder newBuilder() {
    return new Builder(0, "undefined");
  }

  public static Builder newBuilder(ZookeeperResourceStoreConfig config) {
    Preconditions.checkNotNull(config);
    return new Builder(config.timeToLife, config.basePath);
  }

  public static final class Builder {
    private long timeToLife;
    private String basePath;

    private Builder(long timeToLife, String basePath) {
      this.timeToLife = timeToLife;
    }

    public Builder withTimeToLife(int timeToLife) {
      Preconditions.checkArgument(timeToLife >= 0, "negative ttl");
      this.timeToLife = timeToLife;
      return this;
    }

    public Builder withBasePath(String basePath) {
      Preconditions.checkNotNull(basePath);
      this.basePath = basePath;
      return this;
    }

    public ZookeeperResourceStoreConfig create() {
      return new ZookeeperResourceStoreConfig(timeToLife, basePath);
    }
  }
}
