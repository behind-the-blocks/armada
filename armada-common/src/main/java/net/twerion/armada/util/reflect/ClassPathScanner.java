package net.twerion.armada.util.reflect;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.reflect.ClassPath;

public final class ClassPathScanner implements AutoCloseable {
  private Collection<Class<?>> loadedClasses;

  private ClassPathScanner(Collection<Class<?>> loadedClasses) {
    this.loadedClasses = loadedClasses;
  }

  public <E> Stream<Class<E>> findSubTypes(Class<E> superType) {
    return loadedClasses.stream()
      .filter(superType::isAssignableFrom)
      .map(this::unsafeTypeCast);
  }

  private <E> Class<E> unsafeTypeCast(Class<?> type) {
    return (Class<E>) type;
  }

  @Override
  public void close() {
    loadedClasses.clear();
  }

  public static ClassPathScanner create(ClassPath path) {
    return create(path.getTopLevelClasses());
  }

  public static ClassPathScanner createInPackage(
    ClassPath path, String packageName) {

    return create(path.getTopLevelClasses(packageName));
  }

  private static ClassPathScanner create(Collection<ClassPath.ClassInfo> classInfos) {
    return new ClassPathScanner(
      classInfos.stream()
        .map(ClassPath.ClassInfo::load)
        .collect(Collectors.toList())
    );
  }
}
