// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.analysis;

import net.twerion.armada.advisor.resource.ResourceKind;
import net.twerion.armada.advisor.resource.ResourceUsage;

public final class TestAnalysis implements Analysis {

  @Override
  public void reportUsage(
      ResourceKind kind,
      ResourceUsage usage
  ) {

  }

  @Override
  public void reportUsage(
      ResourceKind kind,
      ResourceUsage usage,
      int percentage
  ) {

  }

  @Override
  public void close() { }
}
