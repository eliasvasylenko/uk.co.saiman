package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.EXPERIMENT_SCHEDULE;

public class ExperimentSchedulingEvent extends ExperimentEvent {
  @Override
  public ExperimentEventKind kind() {
    return EXPERIMENT_SCHEDULE;
  }
}
