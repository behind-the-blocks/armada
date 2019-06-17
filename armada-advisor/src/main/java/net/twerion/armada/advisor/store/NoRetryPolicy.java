package net.twerion.armada.advisor.store;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;

public final class NoRetryPolicy implements RetryPolicy {
  private NoRetryPolicy() {}

  @Override
  public boolean allowRetry(int retryCount, long elapsedTimeMs, RetrySleeper sleeper) {
    return false;
  }

  public static NoRetryPolicy create() {
    return new NoRetryPolicy();
  }
}
