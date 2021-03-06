// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor;

public final class AdvisorConfig {
  private int cycleRate;

  private AdvisorConfig(int cycleRate) {
    this.cycleRate = cycleRate;
  }

  public int cycleRate() {
    return cycleRate;
  }
}
