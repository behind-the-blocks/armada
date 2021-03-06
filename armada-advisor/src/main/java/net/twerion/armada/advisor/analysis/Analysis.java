// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.analysis;

import net.twerion.armada.advisor.resource.ResourceKind;
import net.twerion.armada.advisor.resource.ResourceUsage;

public interface Analysis extends AutoCloseable {

  void reportUsage(ResourceKind kind, ResourceUsage usage);

  void reportUsage(ResourceKind kind, ResourceUsage usage, int percentage);

  @Override
  void close();
}
