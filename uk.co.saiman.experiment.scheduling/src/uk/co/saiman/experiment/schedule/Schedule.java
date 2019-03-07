package uk.co.saiman.experiment.schedule;

import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.ConfigurationContext;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.schedule.event.SchedulingEvent;
import uk.co.saiman.experiment.storage.Storage;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.property.IdentityProperty;

public class Schedule {
  private final Scheduler scheduler;
  private final Procedure procedure;
  private final Optional<Products> previousProducts;

  private final Map<ExperimentPath, ScheduledInstruction> scheduledInstructions;

  private final HotObservable<SchedulingEvent> events = new HotObservable<>();

  public Schedule(Scheduler scheduler, Procedure procedure) {
    this.scheduler = scheduler;
    this.procedure = procedure;

    this.previousProducts = scheduler.getProducts();
  }

  public Procedure getProcedure() {
    return procedure;
  }

  private Stream<ProductPath> getInvalidatedProducts() {
    if (previousProducts.isEmpty()) {
      return Stream.empty();
    }
    var previousProducts = this.previousProducts.get();
    if (procedure.instructions().findAny().isEmpty()) {
      return previousProducts.products().map(Product::path);
    }

    /*
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * TODO in order to decide which experiments are invalidated at this point we
     * need to do Conductor#configureVariables for each instruction so that we can
     * find their indirect requirements from the ProcedureContext
     * 
     * TODO obviously the ProcedureContext we pass in should throw with an
     * appropriate error if any of the conductors attempt to mutate the experiment
     * state...
     * 
     * TODO this isn't so surprising, we have to call configureVariables at some
     * point so that we can build our ConductorContext to pass into the
     * Conductor#conduct method. The Schedule seems to be the appropriate place to
     * build this snapshot of the experiment variables.
     *
     *
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */

    Set<ExperimentPath> invalidatedExperiments = new HashSet<>();
    var previousProcedure = previousProducts.getSchedule().getProcedure();
    previousProcedure.instructions().reduce(new HashSet<ExperimentPath>(), (set, path) -> {
      if (path.parent().filter(set::contains).isPresent()
          || procedure
              .instruction(path)
              .filter(previousProcedure.instruction(path).get()::equals)
              .isEmpty()) {
        set.add(path);
      }
      return set;
    }, throwingMerger());

    return previousProducts.stream().flatMap(Products::products).map(Product::path).filter(path -> {
      if (invalidatedProducts.contains(path.getExperimentPath())) {
        ;
      }
    });
  }

  private Stream<ProductPath> getInvalidatedProducts(ExperimentPath path) {
    return procedure.dependents(path).conductor().productions().map(arg0);
  }

  private Stream<ProductPath> getAllProducts(Instruction instruction) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return the conditions produced by the {@link #getProcedure() processed
   *         procedure} which would be invalidated if the current
   *         {@link #getSchedule() scheduled procedure} is {@link #proceed()
   *         proceeded upon}.
   */
  public Stream<ProductPath> conflictingProducts() {
    return invalidatedProducts.stream();
  }

  public Stream<Storage> conflictingStorage() {
    return procedure.paths().map(scheduler.getStorageConfiguration()::locateStorage);
  }

  public boolean isConflictFree() {
    return conflictingProducts().findAny().isEmpty() && conflictingResources().findAny().isEmpty();
  }

  public void unschedule() {
    scheduler.unschedule(this);
  }

  public Products proceed() {
    return scheduler.proceed(this);
  }

  public Observable<SchedulingEvent> events() {
    return events;
  }
}
