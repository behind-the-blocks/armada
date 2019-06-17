package net.twerion.armada.util.concurrent;

import com.google.common.base.Preconditions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutedCyclicRunner implements CyclicRunner {
  private long rate;
  private Runnable task;
  private AtomicBoolean status;
  private ScheduledExecutorService executor;
  private volatile ScheduledFuture<?> runningTask;

  private ExecutedCyclicRunner(
      long rate,
      Runnable task,
      AtomicBoolean status,
      ScheduledExecutorService executor
  ) {
    this.rate = rate;
    this.task = task;
    this.status = status;
    this.executor = executor;
  }

  @Override
  public void run() {
    task.run();
  }

  @Override
  public void start() {
    if (!status.compareAndSet(false, true)) {
      return;
    }
    runningTask = executor.scheduleAtFixedRate(
      this::run, rate, rate, TimeUnit.MILLISECONDS);
  }

  @Override
  public void stop() {
    if (!status.compareAndSet(true, false)){
      return;
    }
    runningTask.cancel(true);
  }

  public static ExecutedCyclicRunner of(long rate, Runnable task) {
    Preconditions.checkNotNull(task);
    Preconditions.checkArgument(rate >= 0, "rate is negative");
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    return new ExecutedCyclicRunner(rate, task, new AtomicBoolean(false), executorService);
  }

  public static ExecutedCyclicRunner create(
    long rate, ScheduledExecutorService executor, Runnable task) {

    Preconditions.checkNotNull(executor);
    Preconditions.checkNotNull(task);
    Preconditions.checkArgument(rate >= 0, "rate is negative");
    return new ExecutedCyclicRunner(rate, task, new AtomicBoolean(false), executor);
  }
}
