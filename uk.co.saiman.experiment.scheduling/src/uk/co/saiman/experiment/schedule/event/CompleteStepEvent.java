package uk.co.saiman.experiment.schedule.event;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.schedule.Products;
import uk.co.saiman.experiment.schedule.Scheduler;

public class CompleteStepEvent extends SchedulingEvent {
  private final Products products;
  private final ExperimentPath path;

  public CompleteStepEvent(Scheduler scheduler, Products products, ExperimentPath path) {
    super(scheduler);
    this.products = products;
    this.path = path;
  }

  public Products products() {
    return products;
  }

  public ExperimentPath path() {
    return path;
  }

  @Override
  public SchedulingEventKind kind() {
    return SchedulingEventKind.COMPLETE_STEP;
  }
}
