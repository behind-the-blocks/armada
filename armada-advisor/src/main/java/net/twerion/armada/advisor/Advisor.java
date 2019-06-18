package net.twerion.armada.advisor;

import java.util.concurrent.Semaphore;

import oshi.SystemInfo;

import net.twerion.armada.Resources;
import net.twerion.armada.advisor.analysis.Analysis;
import net.twerion.armada.advisor.analysis.AnalysisFactory;
import net.twerion.armada.advisor.analysis.ResourceAnalyser;
import net.twerion.armada.advisor.store.ResourceStore;
import net.twerion.armada.advisor.system.SystemInformationCollector;

public final class Advisor {
  private Semaphore runMutex;
  private ResourceStore store;
  private ResourceAnalyser analyser;
  private AnalysisFactory analysisFactory;
  private SystemInformationCollector collector;

  private Advisor(
      Semaphore runMutex,
      ResourceStore store,
      ResourceAnalyser analyser,
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
