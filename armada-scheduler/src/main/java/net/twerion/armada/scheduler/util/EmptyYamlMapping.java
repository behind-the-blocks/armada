package net.twerion.armada.scheduler.util;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;

import java.util.Collection;
import java.util.Collections;

public class EmptyYamlMapping implements YamlMapping {
  private EmptyYamlMapping() {}

  @Override
  public YamlMapping yamlMapping(String key) {
    return null;
  }

  @Override
  public YamlMapping yamlMapping(YamlNode key) {
    return null;
  }

  @Override
  public YamlSequence yamlSequence(String key) {
    return null;
  }

  @Override
  public YamlSequence yamlSequence(YamlNode key) {
    return null;
  }

  @Override
  public String string(String key) {
    return null;
  }

  @Override
  public String string(YamlNode key) {
    return null;
  }

  @Override
  public String indent(int indentation) {
    return null;
  }

  @Override
  public Collection<YamlNode> children() {
    return Collections.emptyList();
  }

  @Override
  public int compareTo(YamlNode target) {
    return 1;
  }

  public static EmptyYamlMapping create() {
    return new EmptyYamlMapping();
  }
}
