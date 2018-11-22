package uk.co.saiman.msapex.experiment;

import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.msapex.experiment.workspace.event.CloseExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.OpenExperimentEvent;

public class IsExperimentOpenExpression {
  @Evaluate
  public boolean evaluate(
      @Optional WorkspaceExperiment experiment,
      @Optional OpenExperimentEvent openEvent,
      @Optional CloseExperimentEvent closeEvent) {
    return experiment != null && experiment.status() == Status.OPEN;
  }
}
