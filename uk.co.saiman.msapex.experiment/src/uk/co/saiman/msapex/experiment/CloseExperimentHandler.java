package uk.co.saiman.msapex.experiment;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;

public class CloseExperimentHandler {
  @CanExecute
  public boolean canExecute(@Optional WorkspaceExperiment experiment) {
    return experiment != null && experiment.status() == Status.OPEN;
  }

  @Execute
  public void execute(WorkspaceExperiment experiment) {
    try {
      experiment.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
