package uk.co.saiman.msapex.experiment;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;

public class OpenExperimentHandler {
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @CanExecute
  public boolean canExecute(@Optional WorkspaceExperiment experiment) {
    return experiment != null && experiment.status() == Status.CLOSED;
  }

  @Execute
  public void execute(WorkspaceExperiment experiment) {
    try {
      experiment.open();

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.openExperimentFailedDialog().toString());
      alert.setHeaderText(text.openExperimentFailedText(experiment).toString());
      alert.setContentText(text.openExperimentFailedDescription().toString());
      alert.showAndWait();
    }
  }
}
