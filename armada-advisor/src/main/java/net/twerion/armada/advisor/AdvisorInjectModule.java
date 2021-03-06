// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public final class AdvisorInjectModule extends AbstractModule  {
  private static final int GLOBAL_FALLBACK_EXECUTOR_SIZE = 2;

  private AdvisorInjectModule() {}

  @Provides
  @Singleton
  Executor createGlobalFallbackExecutor() {
    return Executors.newFixedThreadPool(GLOBAL_FALLBACK_EXECUTOR_SIZE);
  }

  public static AdvisorInjectModule create() {
    return new AdvisorInjectModule();
  }
}
