package uk.co.saiman.experiment.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.schedule.event.SchedulingEvent;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Schedule {
  private final Scheduler scheduler;
  private final Procedure procedure;
  private final Optional<Products> currentProducts;

  private final Map<ExperimentPath<Absolute>, ScheduledInstruction> scheduledInstructions;

  private final HotObservable<SchedulingEvent> events = new HotObservable<>();

  public Schedule(Scheduler scheduler, Procedure procedure) {
    this.scheduler = scheduler;
    this.procedure = procedure;
    this.currentProducts = scheduler.getProducts();

    this.scheduledInstructions = new HashMap<>();
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  public Optional<Products> currentProducts() {
    return currentProducts;
  }

  public Procedure getProcedure() {
    return procedure;
  }

  public Optional<ScheduledInstruction> scheduledInstruction(ExperimentPath<Absolute> path) {
    return Optional.ofNullable(scheduledInstructions.get(path));
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

  Optional<ScheduledInstruction> getParent(ScheduledInstruction scheduledInstruction) {
    return scheduledInstruction
        .productPath()
        .map(ProductPath::getExperimentPath)
        .map(scheduledInstructions::get);
  }

  Stream<ScheduledInstruction> getChildren(ScheduledInstruction scheduledInstruction) {
    return procedure
        .dependentInstructions(scheduledInstruction.experimentPath())
        .map(scheduledInstructions::get);
  }
}
