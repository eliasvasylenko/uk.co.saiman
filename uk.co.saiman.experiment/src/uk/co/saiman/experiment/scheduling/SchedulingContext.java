package uk.co.saiman.experiment.scheduling;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentStep;

public interface SchedulingContext {
  Experiment experiment();

  void commence(ExperimentStep<?> step, Schedule schedule);
}
