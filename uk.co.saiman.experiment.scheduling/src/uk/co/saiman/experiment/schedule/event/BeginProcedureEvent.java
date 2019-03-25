package uk.co.saiman.experiment.schedule.event;

import uk.co.saiman.experiment.schedule.Products;
import uk.co.saiman.experiment.schedule.Scheduler;

public class BeginProcedureEvent extends SchedulingEvent {
  private final Products products;

  public BeginProcedureEvent(Scheduler scheduler, Products products) {
    super(scheduler);
    this.products = products;
  }

  public Products products() {
    return products;
  }

  @Override
  public SchedulingEventKind kind() {
    return SchedulingEventKind.BEGIN_PROCEDURE;
  }
}
