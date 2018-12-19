package uk.co.saiman.experiment.scheduling;

import java.util.Collection;

import uk.co.saiman.experiment.ExperimentStep;

public interface Scheduler {
  void scheduleSteps(Collection<? extends ExperimentStep<?>> steps);

  void unscheduleSteps(Collection<? extends ExperimentStep<?>> steps);
}
