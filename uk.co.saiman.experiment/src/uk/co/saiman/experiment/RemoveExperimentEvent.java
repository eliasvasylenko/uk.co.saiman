package uk.co.saiman.experiment;

import static uk.co.saiman.experiment.ExperimentEventKind.REMOVE;

public class RemoveExperimentEvent extends ExperimentEvent {
  private final Workspace workspace;

  public RemoveExperimentEvent(Experiment experiment, Workspace workspace) {
    super(experiment);
    this.workspace = workspace;
  }

  public Workspace workspace() {
    return workspace;
  }

  @Override
  public ExperimentEventKind kind() {
    return REMOVE;
  }
}
