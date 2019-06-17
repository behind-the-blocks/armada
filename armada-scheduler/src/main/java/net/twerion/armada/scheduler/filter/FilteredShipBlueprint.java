package net.twerion.armada.scheduler.filter;

import com.google.common.base.Preconditions;
import net.twerion.armada.LabelSetFilter;
import net.twerion.armada.ShipBlueprint;

public final class FilteredShipBlueprint {
  private ShipBlueprint blueprint;
  private LabelSetFilter nodeFilter;

  private FilteredShipBlueprint(ShipBlueprint blueprint, LabelSetFilter filter) {
    this.blueprint = blueprint;
    this.nodeFilter = filter;
  }

  public ShipBlueprint blueprint() {
    return blueprint;
  }

  public LabelSetFilter nodeFilter() {
    return nodeFilter;
  }

  public static FilteredShipBlueprint of(ShipBlueprint blueprint) {
    Preconditions.checkNotNull(blueprint);
    LabelSetFilter filter = LabelSetFilter.of(blueprint.getNodeSelectors());
    return new FilteredShipBlueprint(blueprint, filter);
  }
}
