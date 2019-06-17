package net.twerion.armada.advisor;

import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import net.twerion.armada.util.concurrent.CyclicRunner;
import net.twerion.armada.util.concurrent.ExecutedCyclicRunner;

public final class AdvisorFactory {
  private AdvisorConfig config;
  private ScheduledExecutorService executorService;

  @Inject
  private AdvisorFactory(
      AdvisorConfig config,
      ScheduledExecutorService executorService
  ) {
    this.config = config;
    this.executorService = executorService;
  }

  public Advisor createRunningAdvisor() {
    Advisor advisor = createAdvisor();
    createCyclicRunner(advisor).start();
    return advisor;
  }

  private Advisor createAdvisor() {
    return new Advisor();
  }

  private CyclicRunner createCyclicRunner(Advisor advisor) {
    // TODO(merlinosayimwen): Create self stopping (conditional) runner
    return ExecutedCyclicRunner.create(
      config.cycleRate(), executorService, advisor::run);
  }
}
