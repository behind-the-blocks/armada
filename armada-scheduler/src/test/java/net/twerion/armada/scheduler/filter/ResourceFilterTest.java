package net.twerion.armada.scheduler.filter;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.twerion.armada.Node;
import net.twerion.armada.Memory;
import net.twerion.armada.Resources;
import net.twerion.armada.ResourceRequirements;

public final class ResourceFilterTest {
  private ResourceFilter filter;

  @Before
  public void createFilter() {
    this.filter = new ResourceFilter();
  }

  @Test
  public void testNodeFitsResources() {
    int availableMemory = 1000;
    Node node = createNodeWithMemory("test-node-1", availableMemory);

    ResourceRequirements fitting = ResourceRequirements.newBuilder()
      .setBytesOfMemory(availableMemory / 2)
      .build();

    Assert.assertTrue(filter.canFitResources(node, fitting));

    ResourceRequirements exceeding = ResourceRequirements.newBuilder()
      .setBytesOfMemory(availableMemory * 2)
      .build();

    Assert.assertFalse(filter.canFitResources(node, exceeding));
  }

  private Node createNodeWithMemory(String id, int availableMemory) {
    Memory nodeMemory = Memory.newBuilder()
      .setAvailable(availableMemory)
      .build();

    Resources nodeResources = Resources.newBuilder()
      .setMemory(nodeMemory)
      .build();

    return Node.newBuilder()
      .setId(id)
      //.setResources(nodeResources)
      .build();
  }
}