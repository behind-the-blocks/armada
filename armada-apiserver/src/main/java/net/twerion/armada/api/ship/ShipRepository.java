package net.twerion.armada.api.ship;

import net.twerion.armada.Ship;

import java.util.concurrent.CompletableFuture;

public interface ShipRepository {

  CompletableFuture<?> create(Ship ship);

  CompletableFuture<?> updateLifecycleStage(String shipId, Ship.LifecycleStage stage);
}
