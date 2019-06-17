package net.twerion.armada;

import com.google.common.base.Preconditions;

import java.util.Map;

public final class LabelSetFilter {
  private Map<String, String> selectors;
  private LabelSetFilter(Map<String, String> selectors) {
    this.selectors = selectors;
  }

  public boolean filter(LabelSet labels) {
    if (labels.getLabelsCount() == 0) {
      return true;
    }
    for (Label label : labels.getLabelsList()) {
      if (!filterSingle(label)) {
        return false;
      }
    }
    return true;
  }

  public boolean filterSingle(Label label) {
    String expectedValue = selectors.get(label.getName());
    if (expectedValue == null) {
      return false;
    }
    return expectedValue.equals(label.getValue());
  }

  public static LabelSetFilter of(LabelSelectorSet selectors) {
    Preconditions.checkNotNull(selectors);
    // No defensive copy is done because the label selector may never
    // be changed. It is not really immutable, because no defensive
    // copies are done, but creating another copy every time would have
    // negative performance impacts each time the scheduler runs.
    return new LabelSetFilter(selectors.getSelectors());
  }
}
