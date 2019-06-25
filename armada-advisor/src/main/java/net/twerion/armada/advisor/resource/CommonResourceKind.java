// Copyright 2019 the Vicuna Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found in the LICENSE file.

package net.twerion.armada.advisor.resource;

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
