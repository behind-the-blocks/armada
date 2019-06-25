package net.twerion.armada.advisor;

import java.util.concurrent.Semaphore;

import oshi.SystemInfo;

import net.twerion.armada.Resources;
import net.twerion.armada.advisor.analysis.Analysis;
import net.twerion.armada.advisor.analysis.AnalysisFactory;
import net.twerion.armada.advisor.analysis.ResourceAdvisor;
import net.twerion.armada.advisor.store.ResourceStore;
import net.twerion.armada.advisor.system.SystemInformationCollector;

/**
 * Captures the machines resources, analyses and then stores them into the
 * host-nodes zookeeper path.
 * <p>
 * The work of the advisor is run in cycles at a fixed rate. If the current
 * cycle is still running while a new one should start, it is skipped. There
 * may never be two concurrent cycles running.
 *
 * @version 1.0
 *
 * @see AdvisorConfig
 * @see AdvisorFactory
 */
public final class Advisor {
  private Semaphore runMutex;
  private ResourceStore store;
  private ResourceAdvisor analyser;
  private AnalysisFactory analysisFactory;
  private SystemInformationCollector collector;

  private Advisor(
      Semaphore runMutex,
      ResourceStore store,
      ResourceAdvisor analyser,
      AnalysisFactory analysisFactory,
      SystemInformationCollector collector
  ) {
    this.runMutex = runMutex;
    this.store = store;
    this.analyser = analyser;
    this.analysisFactory = analysisFactory;
    this.collector = collector;
  }

  public void run() {
    if (!runMutex.tryAcquire()) {
      // Do not run if the advisor is currently running. The current thread
      // does not wait to prevent a deadlock or some kind of starvation.
      return;
    }
    try {
      runLocked();
    } finally {
      runMutex.release();
    }
  }

  private void runLocked() {
    Resources resources = collectResources();
    store.storeAsync(resources);

    try (Analysis analysis = analysisFactory.createAnalysis()) {
      analyser.analyse(analysis, resources);
    }
  }

  private Resources collectResources() {
    Resources.Builder builder = Resources.newBuilder();
    collector.collect(new SystemInfo(), builder);
    return builder.build();
  }
}
