package uk.co.saiman.experiment.scheduling;

public interface SchedulingStrategy {
  Scheduler provideScheduler(SchedulingContext schedulingContext);
}
