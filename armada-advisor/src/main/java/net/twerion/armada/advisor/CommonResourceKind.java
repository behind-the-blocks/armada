package net.twerion.armada.advisor;

public enum CommonResourceKind implements ResourceKind {
  MEMORY("Memory", Depletability.DEPLETABLE);

  enum Depletability {
    DEPLETABLE,
    NON_DEPLETABLE;
  }

  private String id;
  private Depletability depletability;

  CommonResourceKind(String name, Depletability depletability) {
    this.id = name;
    this.depletability = depletability;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public boolean isDepletable() {
    return depletability == Depletability.DEPLETABLE;
  }
}
