package net.twerion.armada.scheduler.filter;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.twerion.armada.scheduler.host.HostCandidate;

public final class CompositeFilter implements HostFilter {
  private Collection<HostFilter> delegates;
  private CompositeFilter(Collection<HostFilter> delegates) {
    this.delegates = delegates;
  }

  @Override
  public boolean filter(HostCandidate candidate, FilteredShipBlueprint ship) {
    for (HostFilter filter : delegates) {
      if (!filter.filter(candidate, ship)) {
        return false;
      }
    }
    return true;
  }

  public static CompositeFilter of(Collection<HostFilter> delegates) {
    Preconditions.checkNotNull(delegates);
    return new CompositeFilter(ImmutableList.copyOf(delegates));
  }

  public static CompositeFilter of(HostFilter ...delegates) {
    return new CompositeFilter(Arrays.asList(delegates));
  }
}