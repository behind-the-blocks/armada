package net.twerion.armada.util.concurrent;

import com.google.common.base.Preconditions;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public final class CountedCyclicRunner implements CyclicRunner {
  private AtomicInteger count;
  private CyclicRunner delegate;

  private CountedCyclicRunner(AtomicInteger count, CyclicRunner delegate) {
    this.count = count;
    this.delegate = delegate;
  }

  @Override
  public void run() {
    count.incrementAndGet();
    delegate.run();
  }

  @Override
  public void start() {
    delegate.start();
  }

  @Override
  public void stop() {
    delegate.stop();
  }

  public int count() {
    return count.get();
  }

  public static CountedCyclicRunner of(CyclicRunner delegate) {
    Preconditions.checkNotNull(delegate);
    return new CountedCyclicRunner(new AtomicInteger(0), delegate);
  }
}
