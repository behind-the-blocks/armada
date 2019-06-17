package net.twerion.armada.advisor.system;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import net.twerion.armada.Memory;
import net.twerion.armada.Resources;

public final class MemoryInformationCollector implements SystemInformationCollector {
  private MemoryInformationCollector() {}

  @Override
  public void collect(SystemInfo info, Resources.Builder builder) {
    GlobalMemory systemMemory = info.getHardware().getMemory();

    long total = systemMemory.getTotal();
    long available = systemMemory.getAvailable();
    long usedMemory = total - available;

    Memory memory = Memory.newBuilder()
      .setUsed(usedMemory)
      .setTotal(total)
      .setAvailable(available)
      .build();

    builder.setMemory(memory);
  }
}
