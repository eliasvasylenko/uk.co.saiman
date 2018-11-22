package uk.co.saiman.msapex.experiment.workspace.event;

import uk.co.saiman.msapex.experiment.workspace.Workspace;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;

public abstract class WorkspaceEvent {
  private final Workspace workspace;
  private final WorkspaceExperiment experiment;

  public WorkspaceEvent(Workspace workspace, WorkspaceExperiment experiment) {
    this.workspace = workspace;
    this.experiment = experiment;
  }

  public Workspace workspace() {
    return workspace;
  }

  public WorkspaceExperiment experiment() {
    return experiment;
  }

  public abstract WorkspaceEventKind kind();

  @Override
  public String toString() {
    return WorkspaceEvent.class.getSimpleName() + "<" + kind() + ">(" + experiment().name() + ")";
  }
}
