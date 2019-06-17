package net.twerion.armada.advisor.analysis;

import com.google.common.annotations.VisibleForTesting;

import net.twerion.armada.Memory;
import net.twerion.armada.Resources;
import net.twerion.armada.advisor.CommonResourceKind;
import net.twerion.armada.advisor.ResourceUsage;

public final class MemoryAnalyser implements ResourceAnalyser {
  private long healthyPercentageLimit;
  private long criticalPercentageLimit;

  private MemoryAnalyser(
    long healthyPercentageLimit, long criticalPercentageLimit) {

    this.healthyPercentageLimit = healthyPercentageLimit;
    this.criticalPercentageLimit = criticalPercentageLimit;
  }

  @Override
  public void analyse(Analysis analysation, Resources resources) {
    Memory memory = resources.getMemory();
    int percentage = calculatePercentage(memory);
    ResourceUsage usage = usageOfPercentage(percentage);
    analysation.reportUsage(CommonResourceKind.MEMORY, usage, percentage);
  }

  private int calculatePercentage(Memory memory) {
    return (int) (memory.getUsed() * (100 / memory.getAvailable()));
  }

  @VisibleForTesting
  ResourceUsage usageOfPercentage(int percentage) {
    if (percentage <= healthyPercentageLimit) {
      return ResourceUsage.HEALTHY;
    }
    if (percentage <= criticalPercentageLimit) {
      return ResourceUsage.CRITICAL;
    }
    return ResourceUsage.SEVERE;
  }
}