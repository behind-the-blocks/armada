// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.store;

import com.google.common.base.Preconditions;

import org.apache.curator.RetryPolicy;

public final class ZookeeperResourceStoreConfig {
  static final String PATH_NODE_ID_PLACEHOLDER = "${node_id}";

  private long timeToLife;
  private String pathFormat;
  private String connectionString;
  private RetryPolicy retryPolicy;

  private ZookeeperResourceStoreConfig(
    long timeToLife,
    String pathFormat,
    String connectionString,
    RetryPolicy retryPolicy) {

    this.timeToLife = timeToLife;
    this.pathFormat = pathFormat;
    this.connectionString = connectionString;
    this.retryPolicy = retryPolicy;
  }

  public long timeToLife() {
    return timeToLife;
  }

  public String pathFormat() {
    return pathFormat;
  }

  public String connectionString() {
    return connectionString;
  }

  public RetryPolicy retryPolicy() {
    return retryPolicy;
  }

  public static Builder newBuilder() {
    return new Builder(
      0, "undefined","undefined", NoRetryPolicy.create());
  }

  public static Builder newBuilder(ZookeeperResourceStoreConfig config) {
    Preconditions.checkNotNull(config);
    return new Builder(
      config.timeToLife,
      config.pathFormat,
      config.connectionString,
      config.retryPolicy
    );
  }

  public static final class Builder {
    private long timeToLife;
    private String pathFormat;
    private String connectionString;
    private RetryPolicy retryPolicy;

    private Builder(
        long timeToLife,
        String pathFormat,
        String connectionString,
        RetryPolicy retryPolicy
    ) {
      this.timeToLife = timeToLife;
      this.pathFormat = pathFormat;
      this.connectionString = connectionString;
      this.retryPolicy = retryPolicy;
    }

    public Builder withConnectionString(String connectionString) {
      Preconditions.checkNotNull(connectionString);
      this.connectionString = connectionString;
      return this;
    }

    public Builder withRetryPolicy(RetryPolicy retryPolicy) {
      Preconditions.checkNotNull(retryPolicy);
      this.retryPolicy = retryPolicy;
      return this;
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
      return new ZookeeperResourceStoreConfig(
        timeToLife, pathFormat, connectionString, retryPolicy);
    }
  }
}
