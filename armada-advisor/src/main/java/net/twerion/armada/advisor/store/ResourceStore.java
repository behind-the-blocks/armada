package net.twerion.armada.advisor.store;

import net.twerion.armada.Resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface ResourceStore {

  void store(Resources resources);

  CompletableFuture<?> storeAsync(Resources resources);

  CompletableFuture<?> storeAsync(Resources resources, Executor executor);
}
