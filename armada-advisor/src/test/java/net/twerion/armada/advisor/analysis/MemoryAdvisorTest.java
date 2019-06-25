// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.analysis;

import org.junit.Assert;
import org.junit.Test;

import net.twerion.armada.Memory;
import net.twerion.armada.advisor.resource.ResourceUsage;

public final class MemoryAdvisorTest {

  @Test
  public void testPercentageCalculation() {
    MemoryAdvisor advisor = new MemoryAdvisor(0, 0);
    int percentage = advisor.calculatePercentage(
      createMemory(100, 20)
    );
    Assert.assertEquals(20, percentage);
  }

  @Test
  public void testUsageCalculation() {
    MemoryAdvisor advisor = new MemoryAdvisor(75, 85);

    int healthyPercentage = advisor.calculatePercentage(createMemory(100, 74));
    Assert.assertEquals(ResourceUsage.HEALTHY, advisor.calculateUsage(healthyPercentage));

    int criticalPercentage = advisor.calculatePercentage(createMemory(100, 80));
    Assert.assertEquals(ResourceUsage.CRITICAL, advisor.calculateUsage(criticalPercentage));

    int severePercentage = advisor.calculatePercentage(createMemory(100, 90));
    Assert.assertEquals(ResourceUsage.SEVERE, advisor.calculateUsage(severePercentage));
  }

  private Memory createMemory(int available, int used) {
    return Memory.newBuilder()
      .setAvailable(available)
      .setUsed(used)
      .build();
  }
}