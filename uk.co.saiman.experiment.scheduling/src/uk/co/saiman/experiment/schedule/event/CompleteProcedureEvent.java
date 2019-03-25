package uk.co.saiman.experiment.schedule.event;

import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.schedule.Scheduler;

public class CompleteProcedureEvent extends SchedulingEvent {
  private final Procedure procedure;

  public CompleteProcedureEvent(Scheduler scheduler, Procedure procedure) {
    super(scheduler);
    this.procedure = procedure;
  }

  public Procedure procedure() {
    return procedure;
  }

  @Override
  public SchedulingEventKind kind() {
    return SchedulingEventKind.INTERRUPT;
  }
}
