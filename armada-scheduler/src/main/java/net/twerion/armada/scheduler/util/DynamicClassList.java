package net.twerion.armada.scheduler.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.google.common.reflect.ClassPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.twerion.armada.util.reflect.ClassPathScanner;

public class DynamicClassList<E> {
  private Logger logger;
  private String packageName;
  private Class<E> superType;
  private ClassLoader classLoader;
  private Collection<Class<? extends E>> excluded;
  private Collection<Class<? extends E>> fallback;

  private DynamicClassList(
      Logger logger,
      String packageName,
      Class<E> superType,
      ClassLoader classLoader,
      Collection<Class<? extends E>> excluded,
      Collection<Class<? extends E>> fallback
  ) {
    this.logger = logger;
    this.superType = superType;
    this.packageName = packageName;
    this.classLoader = classLoader;
    this.excluded = excluded;
    this.fallback = fallback;
  }

  public Collection<Class<? extends E>> listTypes() {
    try {
      ClassPath classPath = ClassPath.from(classLoader);
      ClassPathScanner filterPackage = ClassPathScanner.createInPackage(
        classPath, packageName
      );
      return filterPackage
        .findSubTypes(superType)
        .filter(this::isNotExcluded)
        .collect(Collectors.toList());
    } catch (IOException classPathCreationException) {
      logger.warn("Can not read ClassPath of class {}", superType.getName());
      logger.warn("Loading classes manually...");
      return fallback;
    }
  }

  private boolean isNotExcluded(Class<? extends E> type) {
    return !excluded.contains(type);
  }

  public static <E> Builder<E>  newBuilder(Class<E> type) {
    DynamicClassList<E> prototype = new DynamicClassList<>(
      null, // Builder does not use the logger
      null, // Builder sets the package
      type,
      null, // Builder sets the classLoader
      Lists.newArrayList(),
      Lists.newArrayList()
    );
    return new Builder<>(prototype).withSuperType(type);
  }

  public static <E> Builder<E> newBuilder(DynamicClassList<E> prototype) {
    Preconditions.checkNotNull(prototype);
    return new Builder<>(prototype);
  }

  public static final class Builder<E> {
    private DynamicClassList<E> prototype;

    private Builder(DynamicClassList<E> prototype) {
      this.prototype = prototype;
    }

    public Builder<E> withSuperType(Class<E> type) {
      Preconditions.checkNotNull(type);
      prototype.superType = type;
      if (prototype.packageName == null) {
        prototype.packageName = type.getPackage().getName();
      }
      if (prototype.classLoader == null) {
        prototype.classLoader = type.getClassLoader();
      }
      return this;
    }

    public Builder<E> withClassLoader(ClassLoader loader) {
      Preconditions.checkNotNull(loader);
      prototype.classLoader = loader;
      return this;
    }

    public Builder<E> withPackage(String packageName) {
      Preconditions.checkNotNull(packageName);
      this.prototype.packageName = packageName;
      return this;
    }

    public Builder<E> withExcluded(Collection<Class<? extends E>> types) {
      Preconditions.checkNotNull(types);
      prototype.excluded = Lists.newArrayList(types);
      return this;
    }

    public Builder<E> addExcluded(Class<? extends E> type) {
      Preconditions.checkNotNull(type);
      prototype.excluded.add(type);
      return this;
    }

    public Builder<E> withFallback(Collection<Class<? extends E>> types) {
      Preconditions.checkNotNull(types);
      prototype.fallback = Lists.newArrayList(types);
      return this;
    }

    public Builder<E> addFallback(Class<? extends E> type) {
      Preconditions.checkNotNull(type);
      prototype.fallback.add(type);
      return this;
    }

    public DynamicClassList<E> create() {
      Preconditions.checkNotNull(prototype.superType);
      Preconditions.checkNotNull(prototype.packageName);
      Preconditions.checkNotNull(prototype.classLoader);
      Logger logger = LogManager.getLogger(DynamicClassList.class);
      return new DynamicClassList<>(
        logger,
        prototype.packageName,
        prototype.superType,
        prototype.classLoader,
        ImmutableList.copyOf(prototype.excluded),
        ImmutableList.copyOf(prototype.fallback)
      );
    }
  }
}
