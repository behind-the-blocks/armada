package net.twerion.armada.scheduler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;

import net.twerion.armada.scheduler.util.EmptyYamlMapping;
import net.twerion.armada.scheduler.util.DynamicClassList;
import net.twerion.armada.scheduler.util.YamlTypeSequenceReader;
import net.twerion.armada.scheduler.filter.HostFilter;
import net.twerion.armada.scheduler.filter.CompositeFilter;
import net.twerion.armada.scheduler.filter.NodeShipSelectorFilter;
import net.twerion.armada.scheduler.filter.ResourceFilter;
import net.twerion.armada.scheduler.filter.ShipNodeSelectorFilter;

public final class SchedulerConfigReader {
  private Logger logger;
  private Injector injector;
  private YamlMapping yaml;

  private SchedulerConfigReader(
    Logger logger, Injector injector, YamlMapping yaml) {

    this.logger = logger;
    this.injector = injector;
    this.yaml = yaml;
  }

  public SchedulerConfig readSchedulerConfig() {
    return null;
  }

  private YamlMapping filterMapping() {
    YamlMapping filter = yaml.yamlMapping("filter");
    if (filter == null) {
      return EmptyYamlMapping.create();
    }
    return filter;
  }

  public HostFilter createHostFilter() {
    if (isUsingAllHostFilters()) {
      return createFilterOfClasses(listCommonHostFilters());
    }
    YamlSequence sequence = filterMapping().yamlSequence("steps");
    if (sequence == null) {
      logger.warn("The sequence filter.field can not be read.");
      logger.warn("Using the default builtin filters...");
      return createFilterOfClasses(listCommonHostFilters());
    }
    YamlTypeSequenceReader<HostFilter> sequenceReader =
      YamlTypeSequenceReader.create(HostFilter.class, sequence);

    return createFilterOfClasses(sequenceReader.readTypeList());
  }

  private boolean isUsingAllHostFilters() {
    return "all".equals(filterMapping().string("steps"));
  }

  private Collection<Class<? extends HostFilter>>  listCommonHostFilters() {
    DynamicClassList<HostFilter> hostFilterClasses =
      DynamicClassList.newBuilder(HostFilter.class)
        .addExcluded(CompositeFilter.class)
        .withFallback(listCommonHostFiltersStatically())
        .create();

    return hostFilterClasses.listTypes();
  }

  private HostFilter createFilterOfClasses(Collection<Class<? extends HostFilter>> classes) {
    logger.info("Configured following filter classes:");
    Consumer<Class<?>> logPrinter = type -> logger.info("  - {}", type.getName());

    Collection<HostFilter> filters = classes.stream()
        .peek(logPrinter)
        .map(injector::getInstance)
        .collect(Collectors.toList());

    return CompositeFilter.of(filters);
  }

  private Collection<Class<? extends HostFilter>> listCommonHostFiltersStatically() {
    // TODO(merlinosayimwen): Update this when adding HostFilter.
    return Arrays.asList(
      NodeShipSelectorFilter.class,
      ShipNodeSelectorFilter.class,
      ResourceFilter.class
    );
  }

  private Collection<String> readHostFilterClassNameList() {
    return Collections.emptyList();
  }

  public static void main(String[] arguments) throws IOException {
    YamlMapping rootMapping = Yaml.createYamlInput("").readYamlMapping();
    SchedulerConfigReader reader = new SchedulerConfigReader(
      LogManager.getLogger("test"), Guice.createInjector(), rootMapping);

    reader.createHostFilter();
  }
}