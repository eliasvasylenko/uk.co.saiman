package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.CHANGE_VARIABLE;

public class ChangeVariableEvent extends ExperimentStepEvent {
  @Override
  public ExperimentEventKind kind() {
    return CHANGE_VARIABLE;
  }
}
