package uk.co.saiman.experiment.schedule.event;

import uk.co.saiman.experiment.schedule.Scheduler;

public abstract class SchedulingEvent {
  private final Scheduler scheduler;

  public SchedulingEvent(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Scheduler scheduler() {
    return scheduler;
  }

  public abstract SchedulingEventKind kind();
}
