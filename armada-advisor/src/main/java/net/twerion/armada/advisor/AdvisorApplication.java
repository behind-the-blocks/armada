package net.twerion.armada.advisor;

import oshi.SystemInfo;

public final class AdvisorApplication {
  private AdvisorApplication() {}

  public static void main(String[] arguments) {
    SystemInfo system = new SystemInfo();
    long availableMemory = system.getHardware().getMemory().getAvailable();
  }
}
