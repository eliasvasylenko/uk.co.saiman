package uk.co.saiman.experiment.schedule;

import java.io.IOException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.storage.StorageConfiguration;

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

  private final SortedMap<ExperimentPath<Absolute>, InstructionProgress> progress;

  public Scheduler(StorageConfiguration<?> storageConfiguration) {
    this.storageConfiguration = storageConfiguration;
    this.schedule = null;
    this.products = null;
    this.progress = new TreeMap<>();
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
    return schedule;
  }

  public synchronized Optional<Schedule> scheduleReset() {
    return getProducts().map(Products::getProcedure).map(this::schedule);
  }

  private void assertFresh(Schedule schedule) {
    if (this.schedule != schedule) {
      throw new SchedulingException("Schedule is stale");
    }
  }

  synchronized void unschedule(Schedule schedule) {
    assertFresh(schedule);

    this.schedule = null;
  }

  synchronized Products proceed(Schedule schedule) {
    assertFresh(schedule);

    var procedure = schedule.getProcedure();

    var progressIterator = progress.entrySet().iterator();
    while (progressIterator.hasNext()) {
      var progress = progressIterator.next();

      procedure
          .instruction(progress.getKey())
          .ifPresentOrElse(progress.getValue()::updateInstruction, () -> {
            progress.getValue().interrupt();
            progressIterator.remove();
          });
    }
    procedure
        .instructions()
        .filter(progress::containsKey)
        .forEach(
            path -> progress
                .put(path, new InstructionProgress(this, path, procedure.instruction(path).get())));

    return products = new Products(this);
  }

  private void assertFresh(Products products) {
    if (this.products != products) {
      throw new SchedulingException("Scheduler products are stale");
    }
  }

  synchronized void interrupt(Products products) {
    assertFresh(products);

    // TODO cancel if we're processing
  }

  synchronized void clear(Products products) throws IOException {
    assertFresh(products);

    interrupt(products);
    this.products = null;
    storageConfiguration
        .locateStorage(
            ExperimentPath.defineAbsolute().resolve(products.getSchedule().getProcedure().id()))
        .deallocate();
  }
}
