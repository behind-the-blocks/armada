package net.twerion.armada.advisor;

public final class AdvisorConfig {
  private int cycleRate;

  private AdvisorConfig(int cycleRate) {
    this.cycleRate = cycleRate;
  }

  public int cycleRate() {
    return cycleRate;
  }
}
