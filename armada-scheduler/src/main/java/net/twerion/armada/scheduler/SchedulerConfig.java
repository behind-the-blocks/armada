package net.twerion.armada.scheduler;

public final class SchedulerConfig {
  public static final int UNLIMITED = -1;

  private int hostCandidateLimit;
  private int filteredHostCandidateLimit;
  private int prioritizedHostCandidateLimit;

  private SchedulerConfig(
      int hostCandidateLimit,
      int filteredHostCandidateLimit,
      int prioritizedHostCandidateLimit
  ) {
    this.hostCandidateLimit = hostCandidateLimit;
    this.filteredHostCandidateLimit = filteredHostCandidateLimit;
    this.prioritizedHostCandidateLimit = prioritizedHostCandidateLimit;
  }

  public int hostCandidateLimit() {
    return hostCandidateLimit;
  }

  public int filteredHostCandidateLimit() {
    return filteredHostCandidateLimit;
  }

  public int prioritizedHostCandidateLimit() {
    return prioritizedHostCandidateLimit;
  }

  public static Builder newBuilder() {
    return newBuilder(new SchedulerConfig(
      UNLIMITED, UNLIMITED, UNLIMITED
    ));
  }

  public static Builder newBuilder(SchedulerConfig prototype) {
    return new Builder(prototype);
  }

  public static final class Builder {
    private SchedulerConfig prototype;

    private Builder(SchedulerConfig prototype) {
      this.prototype = prototype;
    }

    public Builder withHostCandidateLimit(int hostCandidateLimit) {
      prototype.hostCandidateLimit = normalizeLimit(hostCandidateLimit);
      return this;
    }

    public Builder withFilteredHostCandidateLimit(
      int filteredHostCandidateLimit) {

      prototype.filteredHostCandidateLimit = normalizeLimit(filteredHostCandidateLimit);
      return this;
    }

    public Builder withPrioritizedHostCandidateLimit(
      int prioritizedHostCandidateLimit) {

      prototype.prioritizedHostCandidateLimit =
        normalizeLimit(prioritizedHostCandidateLimit);

      return this;
    }

    private int normalizeLimit(int limit) {
      return limit < UNLIMITED ? UNLIMITED : limit;
    }

    public SchedulerConfig create() {
      return new SchedulerConfig(
        prototype.hostCandidateLimit,
        prototype.filteredHostCandidateLimit,
        prototype.prioritizedHostCandidateLimit
      );
    }
  }
}
