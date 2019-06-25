// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor;

import oshi.SystemInfo;

public final class AdvisorApplication {
  private AdvisorApplication() {}

  public static void main(String[] arguments) {
    SystemInfo system = new SystemInfo();
    long availableMemory = system.getHardware().getMemory().getAvailable();
  }
}
