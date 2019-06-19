/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* Modified version of curators SimpleDistributedQueue. */

package net.twerion.armada.scheduler.queue;

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.EnsureContainers;
import org.apache.curator.utils.ZKPaths;

import javax.annotation.Nullable;

public final class ZookeeperQueue {
  private static final Logger LOG = LogManager.getLogger(ZookeeperQueue.class);
  private static final String PREFIX = "qn-";

  private CuratorFramework client;
  private String path;
  private EnsureContainers ensureContainers;

  private ZookeeperQueue(
      CuratorFramework client,
      String path,
      EnsureContainers ensureContainers
  ) {
    this.client = client;
    this.path = path;
    this.ensureContainers = ensureContainers;
  }

  public Optional<byte[]> head() {
    return Optional.ofNullable(readHeadElement());
  }

  public Optional<byte[]> popHead() {
    return Optional.ofNullable(popHeadElement());
  }

  public void removeHead() {
    removeHeadElement();
  }

  @Nullable
  public byte[] take() throws Exception {
    return internalPoll(0, null);
  }

  public boolean tryInsert(byte[] data) {
    String newNodePath = ZKPaths.makePath(path, PREFIX);
    try {
      client.create()
        .creatingParentContainersIfNeeded()
        .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
        .forPath(newNodePath, data);
    } catch (Exception failure) {

      return false;
    }
    return true;
  }

  public byte[] poll(long timeout, TimeUnit unit) throws Exception {
    return internalPoll(timeout, unit);
  }

  protected void ensurePath() throws Exception {
    ensureContainers.ensure();
  }

  private byte[] internalPoll(long timeout, @Nullable TimeUnit unit) throws Exception {
    if (!tryToEnsurePath()) {
      return null;
    }
    long startMs = System.currentTimeMillis();
    boolean hasTimeout = (unit != null);
    long maxWaitMs = hasTimeout ? TimeUnit.MILLISECONDS.convert(timeout, unit) : Long.MAX_VALUE;
    for (; ; ) {
      final CountDownLatch latch = new CountDownLatch(1);
      Watcher watcher = event -> latch.countDown();
      byte[] bytes;
      try {
        bytes = popHeadElementWithWatcher(watcher);
      } catch (NoSuchElementException dummy) {
        LOG.debug("Parent containers appear to have lapsed - recreate and retry");
        ensureContainers.reset();
        continue;
      }
      if (bytes != null) {
        return bytes;
      }
      if (hasTimeout) {
        long elapsedMs = System.currentTimeMillis() - startMs;
        long thisWaitMs = maxWaitMs - elapsedMs;
        if (thisWaitMs <= 0) {
          return null;
        }
        latch.await(thisWaitMs, TimeUnit.MILLISECONDS);
      } else {
        latch.await();
      }
    }
  }

  private boolean tryToEnsurePath() {
    try {
      ensurePath();
      return true;
    } catch (Exception ensureFailure) {
      LOG.error("Failed ensuring the path", ensureFailure);
      return false;
    }
  }

  private void removeHeadElement() {
    String headId = findHeadId();
    if (headId == null) {
      return;
    }
    String headPath = ZKPaths.makePath(path, headId);
    try {
      client.delete().forPath(headPath);
    } catch (KeeperException.NoNodeException noSuchNode) {
      // Node has already been removed. Ignore exception...
    } catch (Exception otherFailure) {
      String errorMessage = String.format(
        "Failed deleting the children for path %s", headPath
      );
      LOG.error(errorMessage, otherFailure);
    }
  }

  @Nullable
  private byte[] readHeadElement() {
  String headId = findHeadId();
    if (headId == null) {
      return null;
    }
    String headPath = ZKPaths.makePath(path, headId);
    try {
      return client.getData().forPath(path);
    } catch (KeeperException.NoNodeException noSuchNode) {
      // Node has already been removed. Ignore exception...
    } catch (Exception otherFailure) {
      String errorMessage = String.format(
        "Failed deleting the children for path %s", headPath
      );
      LOG.error(errorMessage, otherFailure);
    }
    return null;
  }

  @Nullable
  private byte[] popHeadElement() {
    String headId = findHeadId();
    if (headId == null) {
      return null;
    }
    return popHeadWithId(headId);
  }

  @Nullable byte[] popHeadElementWithWatcher(Watcher watcher) {
    String headId = findHeadIdWithWatcher(watcher);
    if (headId == null) {
      return null;
    }
    return popHeadWithId(headId);
  }

  private byte[] popHeadWithId(String headId) {
    String headPath = ZKPaths.makePath(path, headId);
    try {
      byte[] data = client.getData().forPath(headPath);
      client.delete().forPath(headPath);
      return data;
    } catch (KeeperException.NoNodeException noSuchNode) {
      // Node has already been removed. Ignore exception...
    } catch (Exception otherFailure) {
      String errorMessage = String.format(
        "Failed deleting the children for path %s", headPath
      );
      LOG.error(errorMessage, otherFailure);
    }
    return null;
  }

  @Nullable
  private String findHeadId() {
    tryToEnsurePath();

    try {
      List<String> nodes = client.getChildren().forPath(path);
      Collections.sort(nodes);
      return findFirstNonForeign(nodes);
    } catch (KeeperException.NoNodeException noSuchNode) {
      // There is no head to return.
      return null;
    } catch (Exception otherFailure) {
      String errorMessage = String.format(
        "Failed getting the children for path %s", path
      );
      LOG.error(errorMessage, otherFailure);
      return null;
    }
  }

  @Nullable
  private String findHeadIdWithWatcher(Watcher watcher) {
    tryToEnsurePath();

    try {
      List<String> nodes = client.getChildren()
          .usingWatcher(watcher)
          .forPath(path);

      Collections.sort(nodes);
      return findFirstNonForeign(nodes);
    } catch (KeeperException.NoNodeException noSuchNode) {
      // There is no head to return.
      return null;
    } catch (Exception otherFailure) {
      String errorMessage = String.format(
        "Failed getting the children for path %s", path
      );
      LOG.error(errorMessage, otherFailure);
      return null;
    }
  }

  @Nullable
  private String findFirstNonForeign(Collection<String> nodes) {
    for (String nodeId : nodes) {
      if (isForeign(nodeId)) {
        LOG.warn("Foreign node in queue path {}", ZKPaths.makePath(path, nodeId));
        continue;
      }
      return nodeId;
    }
    return null;
  }

  private boolean isForeign(String nodeId) {
    return nodeId.startsWith(PREFIX);
  }
}