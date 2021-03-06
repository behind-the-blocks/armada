// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.system;

import oshi.SystemInfo;

import net.twerion.armada.Resources;

public interface SystemInformationCollector {

  void collect(SystemInfo info, Resources.Builder builder);
}