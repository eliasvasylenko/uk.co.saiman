package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.ADD;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Workspace;

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
