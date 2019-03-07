package uk.co.saiman.experiment.schedule;

import java.io.IOException;
import java.util.Optional;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.schedule.event.InterruptEvent;
import uk.co.saiman.experiment.schedule.event.ProceedEvent;
import uk.co.saiman.experiment.schedule.event.SchedulingEvent;
import uk.co.saiman.experiment.schedule.event.SchedulingEvent;
import uk.co.saiman.experiment.schedule.event.UnscheduleEvent;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A scheduler
 * <p>
 * A scheduler should not be shared between multiple clients. While updates are
 * atomic and thread safe, clients who wish to make sure there are no
 * invalidations before proceeding
 * 
 * @author Elias N Vasylenko
 *
 */
public class Scheduler {
  private final StorageConfiguration<?> storageConfiguration;
  private Schedule schedule;
  private Products products;

  public Scheduler(StorageConfiguration<?> storageConfiguration) {
    this.storageConfiguration = storageConfiguration;
    this.schedule = null;
    this.products = null;
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return storageConfiguration;
  }

  public Optional<Schedule> getSchedule() {
    return Optional.ofNullable(schedule);
  }

  public Optional<Products> getProducts() {
    return Optional.ofNullable(products);
  }

  public synchronized Schedule schedule(Procedure procedure) {
    schedule = new Schedule(this, procedure);
    events.next(new SchedulingEvent(this, schedule));
    return schedule;
  }

  private void assertFresh(Schedule schedule) {
    if (this.schedule != schedule) {
      throw new SchedulingException("Schedule is stale");
    }
  }

  synchronized void unschedule(Schedule schedule) {
    assertFresh(schedule);

    this.schedule = null;
    events.next(new UnscheduleEvent(this, schedule));
  }

  synchronized Products proceed(Schedule schedule) {
    assertFresh(schedule);

    /*
     * TODO if we are already proceeding, we may be able to "join" any changes into
     * the existing process if all conflicting interdependencies are downstream.
     */
    products = new Products(this);
    events.next(new ProceedEvent(this, products));
    return products;
  }

  private void assertFresh(Products products) {
    if (this.products != products) {
      throw new SchedulingException("Scheduler products are stale");
    }
  }

  synchronized void interrupt(Products products) {
    assertFresh(products);

    // TODO cancel if we're processing
    events.next(new InterruptEvent(this, products));
  }

  synchronized void clear(Products products) throws IOException {
    assertFresh(products);

    try {
      interrupt(products);
      this.products = null;
      storageConfiguration
          .locateStorage(ExperimentPath.defineAbsolute().resolve(procedure.id()))
          .deallocate();
    } finally {
      events.next(new ClearProductsEvent());
    }
  }
}
