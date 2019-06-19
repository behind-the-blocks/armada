package net.twerion.armada.scheduler.queue;

import java.util.Optional;

import net.twerion.armada.Ship;

/**
 * Queue of unscheduled ships that is processed by one scheduler at a time.
 */
public interface ShipQueue {

  /**
   * Removes the queues head element after it has been processed.
   * <p>
   * The scheduler will call pop once it has scheduled or is rescheduling the
   * current ship. There is only one scheduler at a time and a scheduler can
   * fail during scheduling of a ship. If he does, the ship will not be
   * dropped or left in the 'CREATED' stage but rather scheduled by the next
   * scheduler that becomes leader. Once a scheduler has successfully processed
   * a ship, it will call this method to commit it. A subsequent call to
   * the next method will return a different ship.
   * <p>
   * If the queue is empty, this method will do nothing.
   */
  void commitHead();

  /**
   * Adds a ship to the queues tail.
   * <p>
   * The rescheduler will call this method to reschedule a ship after is could
   * not be scheduled. This way it will be processed later and has a higher
   * chance of being scheduled successfully.
   *
   * @param element Ship to be added to the queues tail.
   * @return True if the element has been added to the queue.
   */
  boolean addLast(Ship element);

  /**
   * Returns the queues head element, if it is not empty.
   *
   * @return Head of the queue.
   */
  Optional<Ship> head();
}