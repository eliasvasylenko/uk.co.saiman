package uk.co.saiman.experiment.scheduling.concurrent;

import java.util.Collection;

import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.scheduling.Scheduler;
import uk.co.saiman.experiment.scheduling.SchedulingContext;

public class ConcurrentScheduler implements Scheduler {
  private final SchedulingContext schedulingContext;
  private final int maximumConcurrency;

  public ConcurrentScheduler(SchedulingContext schedulingContext, int maximumConcurrency) {
    this.schedulingContext = schedulingContext;
    this.maximumConcurrency = maximumConcurrency;
  }

  @Override
  public void scheduleSteps(Collection<? extends ExperimentStep<?>> steps) {
    // TODO Auto-generated method stub

  }

  @Override
  public void unscheduleSteps(Collection<? extends ExperimentStep<?>> steps) {
    // TODO Auto-generated method stub

  }
}
