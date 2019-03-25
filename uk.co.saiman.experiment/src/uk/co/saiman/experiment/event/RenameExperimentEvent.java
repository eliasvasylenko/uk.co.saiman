package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.RENAME_EXPERIMENT;

public class RenameExperimentEvent extends ExperimentEvent {
  @Override
  public ExperimentEventKind kind() {
    return RENAME_EXPERIMENT;
  }

  public String id() {
    // TODO
    return null;
  }
}
