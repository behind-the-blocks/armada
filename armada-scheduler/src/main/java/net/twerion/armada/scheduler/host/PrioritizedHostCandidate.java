package net.twerion.armada.scheduler.host;

import com.google.common.base.Preconditions;

public final class PrioritizedHostCandidate implements Comparable<PrioritizedHostCandidate> {
  private HostCandidate candidate;
  private float priority;

  private PrioritizedHostCandidate(HostCandidate candidate, float priority) {
    this.candidate = candidate;
    this.priority = priority;
  }

  public HostCandidate candidate() {
    return candidate;
  }

  public float priority() {
    return priority;
  }

  @Override
  public int compareTo(PrioritizedHostCandidate target) {
    return Float.compare(priority, target.priority);
  }

  public static PrioritizedHostCandidate create(HostCandidate candidate, float priority) {
    Preconditions.checkNotNull(candidate);
    Preconditions.checkArgument(priority >= 0, "priority is negative");
    return new PrioritizedHostCandidate(candidate, priority);
  }
}
