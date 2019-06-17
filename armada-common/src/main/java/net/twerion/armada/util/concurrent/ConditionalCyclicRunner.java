package net.twerion.armada.util.concurrent;

import java.util.function.BooleanSupplier;

import com.google.common.base.Preconditions;

public final class ConditionalCyclicRunner implements CyclicRunner {
  private CyclicRunner delegate;
  private BooleanSupplier condition;

  private ConditionalCyclicRunner(CyclicRunner delegate, BooleanSupplier condition) {
    this.delegate = delegate;
    this.condition = condition;
  }

  @Override
  public void start() {
    delegate.start();
  }

  @Override
  public void run() {
    if (!condition.getAsBoolean()) {
      stop();
      return;
    }
    delegate.run();
  }

  @Override
  public void stop() {
    delegate.stop();
  }

  public static ConditionalCyclicRunner create(
    CyclicRunner delegate, BooleanSupplier condition) {

    Preconditions.checkNotNull(delegate);
    Preconditions.checkNotNull(condition);
    return new ConditionalCyclicRunner(delegate, condition);
  }
}
