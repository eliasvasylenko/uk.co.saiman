package uk.co.saiman.experiment;

import static uk.co.saiman.experiment.ExperimentEventKind.ADD;

public class AddExperimentEvent extends ExperimentEvent {
  private final Workspace workspace;

  public AddExperimentEvent(Experiment experiment, Workspace workspace) {
    super(experiment);
    this.workspace = workspace;
  }

  public Workspace workspace() {
    return workspace;
  }

  @Override
  public ExperimentEventKind kind() {
    return ADD;
  }
}
