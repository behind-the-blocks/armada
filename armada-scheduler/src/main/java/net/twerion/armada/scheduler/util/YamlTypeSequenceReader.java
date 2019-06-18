package net.twerion.armada.scheduler.util;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amihaiemil.eoyaml.YamlSequence;

public class YamlTypeSequenceReader<E> {
  @Nullable
  private YamlSequence sequence;
  private Class<E> superType;
  private Logger logger;

  private YamlTypeSequenceReader(
    Logger logger, Class<E> superType, @Nullable YamlSequence sequence) {

    this.logger = logger;
    this.superType = superType;
    this.sequence = sequence;
  }

  public Collection<Class<? extends E>> readTypeList() {
    if (sequence == null) {
      return Collections.emptyList();
    }
    Collection<Class<? extends E>> classes =
      Lists.newArrayListWithExpectedSize(sequence.size());

    for (int index = 0; index < sequence.size(); index++) {
      String className = sequence.string(index);
      Class<? extends E> type = findTypeClassByName(className);
      if (type == null) {
        continue;
      }
      classes.add(type);
    }
    return classes;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  private Class<? extends E> findTypeClassByName(String className) {
    if (className == null) {
      logger.warn("Invalid field in sequence of {} classes", superType.getName());
      return null;
    }
    try {
      Class<?> type = Class.forName(className);
      if (!superType.isAssignableFrom(type)) {
        logger.warn("Skipping field {}, class is not of type {}",
          className, superType.getName());
        return null;
      }
      return (Class<? extends E>) type;
    } catch (ClassNotFoundException noSuchClass) {
      logger.warn("Skipping field {}, class not found", className);
      return null;
    }
  }

  public static <E> YamlTypeSequenceReader<E> create(
    Class<E> superType, YamlSequence sequence) {

    Preconditions.checkNotNull(superType);
    Preconditions.checkNotNull(sequence);
    Logger logger = LogManager.getLogger(YamlTypeSequenceReader.class);
    return new YamlTypeSequenceReader<>(logger, superType, sequence);
  }
}
