/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.experiment.msapex.i18n.ExperimentProperties;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class RemoveExperimentHandler {
  @Inject
  Log log;
  @Inject
  @Service
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
