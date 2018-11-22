
package uk.co.saiman.msapex.experiment;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;

public class RemoveExperimentHandler {
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @Execute
  public void execute(WorkspaceExperiment experiment) {
    Alert confirmation = new Alert(AlertType.CONFIRMATION);
    confirmation.setTitle(text.removeExperimentDialog().toString());
    confirmation.setHeaderText(text.removeExperimentText(experiment).toString());
    confirmation.setContentText(text.removeExperimentConfirmation().toString());

    if (confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
      removeExperiment(experiment);
    }
  }

  private void removeExperiment(WorkspaceExperiment experiment) {
    try {
      experiment.remove();

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.removeExperimentFailedDialog().toString());
      alert.setHeaderText(text.removeExperimentFailedText(experiment).toString());
      alert.setContentText(text.removeExperimentFailedDescription().toString());
      alert.showAndWait();
    }
  }
}
