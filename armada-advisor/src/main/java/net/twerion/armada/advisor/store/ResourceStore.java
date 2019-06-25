// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.store;

import net.twerion.armada.Resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface ResourceStore {

  void store(Resources resources);

  CompletableFuture<?> storeAsync(Resources resources);

  CompletableFuture<?> storeAsync(Resources resources, Executor executor);
}
