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

  /**
   * The amount of time that a resource entry will life in zookeeper. This
   * value should be higher than the advisor's cycle-rate to ensure that small
   * timeouts in the advisor don't lead to the scheduler or any other component
   * missing to get the nodes resources. The nodes created by the advisor are
   * ephemeral, meaning that they are deleted once the advisor's zookeeper
   * connection closes.
   *
   * @return Lifetime of nodes created by the advisor.
   */
  public long timeToLife() {
    return this.timeToLife;
  }

  /**
   * Path to the resource stores node, that may include a placeholder for the
   * nodes id (${node_id}). The client uses this path to create new zookeeper
   * nodes, containing resource stats of the locale machine.
   */
  public String pathFormat() {
    return this.pathFormat;
  }

  /**
   * Connection string used to establish a Zookeeper connection.
   */
  public String connectionString() {
    return this.connectionString;
  }

  /**
   * The policy that the Zookeeper client is using the retry a failed
   * connection attempt. The implementation should normally be a forever-retry,
   * otherwise the advisor process may be killed on a number of failed attempts,
   * allowing systemd to schedule a new advisor process to be spawned.
   *
   * @return Strategy to retry failed connection attempts.
   */
  public RetryPolicy retryPolicy() {
    return this.retryPolicy;
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
