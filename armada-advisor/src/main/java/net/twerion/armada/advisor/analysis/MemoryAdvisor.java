// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.analysis;

import com.google.common.annotations.VisibleForTesting;

import net.twerion.armada.Memory;
import net.twerion.armada.Resources;
import net.twerion.armada.advisor.resource.CommonResourceKind;
import net.twerion.armada.advisor.resource.ResourceUsage;

/**
 * Analyses the systems memory usage and creates a {@code ResourceUsage}
 * kind which is then reported to the {@code Analysis} instance.
 *
 * @see ResourceAdvisor
 */
public final class MemoryAdvisor implements ResourceAdvisor {
  private long healthyPercentageLimit;
  private long criticalPercentageLimit;

  MemoryAdvisor(
      long healthyPercentageLimit,
      long criticalPercentageLimit
  ) {
    this.healthyPercentageLimit = healthyPercentageLimit;
    this.criticalPercentageLimit = criticalPercentageLimit;
  }

  @Override
  public void analyse(Analysis analysis, Resources resources) {
    Memory memory = resources.getMemory();
    int percentage = calculatePercentage(memory);
    ResourceUsage usage = calculateUsage(percentage);
    analysis.reportUsage(CommonResourceKind.MEMORY, usage, percentage);
  }

  @VisibleForTesting
  int calculatePercentage(Memory memory) {
    return (int) (memory.getUsed() * (100 / memory.getAvailable()));
  }

  @VisibleForTesting
  ResourceUsage calculateUsage(int percentage) {
    if (percentage <= healthyPercentageLimit) {
      return ResourceUsage.HEALTHY;
    }
    if (percentage <= criticalPercentageLimit) {
      return ResourceUsage.CRITICAL;
    }
    return ResourceUsage.SEVERE;
  }
}