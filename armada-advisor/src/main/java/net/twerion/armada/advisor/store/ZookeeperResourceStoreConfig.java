package net.twerion.armada.advisor.store;

import com.google.common.base.Preconditions;

public final class ZookeeperResourceStoreConfig {
  public static final String PATH_NODE_ID_PLACEHOLDER = "${node_id}";

  private long timeToLife;
  private String pathFormat;

  private ZookeeperResourceStoreConfig(long timeToLife, String pathFormat) {
    this.timeToLife = timeToLife;
    this.pathFormat = pathFormat;
  }

  public long timeToLife() {
    return timeToLife;
  }

  public String pathFormat() {
    return pathFormat;
  }

  public static Builder newBuilder() {
    return new Builder(0, "undefined");
  }

  public static Builder newBuilder(ZookeeperResourceStoreConfig config) {
    Preconditions.checkNotNull(config);
    return new Builder(config.timeToLife, config.pathFormat);
  }

  public static final class Builder {
    private long timeToLife;
    private String pathFormat;

    private Builder(long timeToLife, String pathFormat) {
      this.timeToLife = timeToLife;
      this.pathFormat = pathFormat;
    }

    public Builder withTimeToLife(int timeToLife) {
      Preconditions.checkArgument(timeToLife >= 0, "negative ttl");
      this.timeToLife = timeToLife;
      return this;
    }

    public Builder withPathFormat(String pathFormat) {
      Preconditions.checkNotNull(pathFormat);
      this.pathFormat = pathFormat;
      return this;
    }

    public ZookeeperResourceStoreConfig create() {
      return new ZookeeperResourceStoreConfig(timeToLife, pathFormat);
    }
  }
}
