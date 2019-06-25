// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import com.google.inject.Provider;
import net.twerion.armada.advisor.analysis.AnalysisFactory;
import net.twerion.armada.advisor.analysis.ResourceAdvisor;
import net.twerion.armada.advisor.store.ResourceStoreFactory;
import net.twerion.armada.advisor.system.SystemInformationCollector;
import net.twerion.armada.util.concurrent.CyclicRunner;
import net.twerion.armada.util.concurrent.ExecutedCyclicRunner;

public final class AdvisorFactory {
  private AdvisorConfig config;
  private AnalysisFactory analysisFactory;
  private ResourceAdvisor resourceAdvisor;
  private ResourceStoreFactory resourceStoreFactory;
  private ScheduledExecutorService executorService;
  private SystemInformationCollector informationCollector;

  @Inject
  private AdvisorFactory(
      AdvisorConfig config,
      AnalysisFactory analysisFactory,
      ResourceStoreFactory resourceStoreFactory,
      ResourceAdvisor resourceAdvisor,
      ScheduledExecutorService executorService,
      SystemInformationCollector informationCollector
  ) {
    this.config = config;
    this.resourceAdvisor = resourceAdvisor;
    this.analysisFactory = analysisFactory;
    this.executorService = executorService;
    this.resourceStoreFactory = resourceStoreFactory;
    this.informationCollector = informationCollector;
  }

  public Advisor createRunningAdvisor() {
    Advisor advisor = createAdvisor();
    createCyclicRunner(advisor).start();
    return advisor;
  }

  private Advisor createAdvisor() {
    return new Advisor(
      new Semaphore(1),
      resourceStoreFactory.createResourceStore(),
      resourceAdvisor,
      analysisFactory,
      informationCollector
    );
  }

  private CyclicRunner createCyclicRunner(Advisor advisor) {
    // TODO(merlinosayimwen): Create self stopping (conditional) runner
    return ExecutedCyclicRunner.create(
      config.cycleRate(), executorService, advisor::run);
  }
}
