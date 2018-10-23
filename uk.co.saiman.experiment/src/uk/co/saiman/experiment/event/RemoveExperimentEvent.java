package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.REMOVE;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Workspace;

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
