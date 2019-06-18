package net.twerion.armada.scheduler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.inject.Injector;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;

import org.apache.logging.log4j.Logger;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;

import net.twerion.armada.util.reflect.ClassPathScanner;
import net.twerion.armada.scheduler.util.EmptyYamlMapping;
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
    return createFilterOfClasses(readFilterSteps());
  }

  private boolean isUsingAllHostFilters() {
    String steps = filterMapping().string("steps");
    if (steps == null) {
      return false;
    }
    return steps.equals("all");
  }

  private HostFilter createFilterOfClasses(
    Collection<Class<? extends HostFilter>> classes) {
    Collection<HostFilter> filters = classes.stream()
        .map(injector::getInstance)
        .collect(Collectors.toList());

    return CompositeFilter.of(filters);
  }

  private Collection<Class<? extends HostFilter>> readFilterSteps() {
    YamlSequence classNames = filterMapping().yamlSequence("steps");
    if (classNames == null) {
      return Collections.emptyList();
    }

    Collection<Class<? extends HostFilter>> classes =
      Lists.newArrayListWithExpectedSize(classNames.size());

    for (int index = 0; index < classNames.size(); index++) {
      String className = classNames.string(index);
      Class<? extends HostFilter> type = findHostFilterClassByName(className);
      if (type == null) {
        continue;
      }
      classes.add(type);
    }
    return classes;
  }

  @Nullable
  private Class<? extends HostFilter> findHostFilterClassByName(String className) {
    if (className == null) {
      logger.warn("Configuration contains invalid filter.steps class-name field");
      return null;
    }
    try {
      Class<?> type = Class.forName(className);
      if (!HostFilter.class.isAssignableFrom(type)) {
        logger.warn("Skipping filter.step entry {}, class is not a HostFilter", className);
        return null;
      }
      return (Class<? extends HostFilter>) type;
    } catch (ClassNotFoundException noSuchClass) {
      logger.warn("Skipping filter.step entry {}, class not found", className);
      return null;
    }
  }

  private Collection<Class<? extends HostFilter>> listCommonHostFilters() {
    Class<HostFilter> hostFilterClass = HostFilter.class;
    try {
      ClassPath classPath = ClassPath.from(hostFilterClass.getClassLoader());
      ClassPathScanner filterPackage = ClassPathScanner.createInPackage(
        classPath, hostFilterClass.getPackage().getName()
      );
      return filterPackage
        .findSubTypes(hostFilterClass)
        .collect(Collectors.toList());
    } catch (IOException classPathCreationException) {
      logger.warn(
        "Can not read ClassPath of class {}.\n"+
        "Loading host filters manually...", hostFilterClass.getName());
      return listCommonHostFiltersStatically();
    }
  }

  private Collection<Class<? extends HostFilter>> listCommonHostFiltersStatically() {
    // TODO(merlinosayimwen): Udate this when adding HostFilter.
    return Arrays.asList(
      NodeShipSelectorFilter.class,
      ShipNodeSelectorFilter.class,
      ResourceFilter.class
    );
  }

  private Collection<String> readHostFilterClassNameList() {
    return Collections.emptyList();
  }
}