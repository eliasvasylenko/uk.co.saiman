package uk.co.saiman.experiment.schedule.event;

import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.schedule.Scheduler;

public class TerminateEvent extends SchedulingEvent {
  private final Procedure procedure;

  public TerminateEvent(Scheduler scheduler, Procedure procedure) {
    super(scheduler);
    this.procedure = procedure;
  }

  public Procedure procedure() {
    return procedure;
  }

  @Override
  public SchedulingEventKind kind() {
    return SchedulingEventKind.TERMINATE;
  }
}
