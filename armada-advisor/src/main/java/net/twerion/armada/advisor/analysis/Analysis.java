package net.twerion.armada.advisor.analysis;

import net.twerion.armada.advisor.resource.ResourceKind;
import net.twerion.armada.advisor.resource.ResourceUsage;

public interface Analysis extends AutoCloseable {

  void reportUsage(ResourceKind kind, ResourceUsage usage);

  void reportUsage(ResourceKind kind, ResourceUsage usage, int percentage);

  @Override
  void close();
}
