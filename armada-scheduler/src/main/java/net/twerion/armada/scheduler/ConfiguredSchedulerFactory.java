package net.twerion.armada.scheduler;

import java.nio.file.Path;

public final class ConfiguredSchedulerFactory implements SchedulerFactory {
  private Path configurationPath;
  private ConfiguredSchedulerFactory(Path configurationPath) {
    this.configurationPath = configurationPath;
  }

  @Override
  public Scheduler createScheduler() {
    return null;
  }
}
