package net.twerion.armada.advisor.system;

import oshi.SystemInfo;

import net.twerion.armada.Resources;

public interface SystemInformationCollector {

  void collect(SystemInfo info, Resources.Builder builder);
}