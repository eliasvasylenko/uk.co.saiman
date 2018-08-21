/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import static java.lang.String.format;
import static java.util.Comparator.reverseOrder;
import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;
import static uk.co.saiman.fx.FxUtilities.wrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.e4.core.di.annotations.Execute;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.properties.Localized;
import uk.co.saiman.utility.Named;

/**
 * Add an experiment to the workspace
 * 
 * @author Elias N Vasylenko
 */
public class RenameExperimentHandler {
  @Execute
  void execute(
      Workspace workspace,
      @Localize ExperimentProperties text,
      @AdaptNamed(ACTIVE_SELECTION) ExperimentNode<?, ?> node) {
    Object state = node.getState();
    if (state instanceof Named) {
      Named named = (Named) state;

      requestExperimentNameDialog(
          workspace,
          text.renameExperiment(),
          text.renameExperimentName(named.getName())).ifPresent(name -> {
            if (workspace.getExperiment(name).isPresent()) {
              // this should have been detected in the requestExperimentNameDialog logic.
              throw new ExperimentException(format("Experiment already exists with id %s", name));
            }

            /*
             * TODO we can no longer check if data already exists at the location.
             * RenameExperiment.confirmOverwriteIfNecessary(newLocation, text);
             */

            named.setName(name);
          });
    }
  }

  static Optional<String> requestExperimentNameDialog(
      Workspace workspace,
      Localized<String> title,
      Localized<String> header) {
    TextInputDialog nameDialog = new TextInputDialog();
    nameDialog.titleProperty().bind(wrap(title));
    nameDialog.headerTextProperty().bind(wrap(header));
    GridPane content = (GridPane) nameDialog.getDialogPane().getContent();
    content.add(new Label("This is where we need to put format error messages..."), 0, 0);
    content.getChildren().remove(nameDialog.getEditor());
    content.add(nameDialog.getEditor(), 0, 1);

    Button okButton = (Button) nameDialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.setDisable(true);
    nameDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

      boolean exists = workspace
          .getExperiments()
          .anyMatch(e -> e.getState().getName().equals(newValue));

      boolean isValid = ExperimentConfiguration.isNameValid(newValue);

      okButton.setDisable(!isValid || exists);
    });

    return nameDialog.showAndWait();
  }

  static void confirmOverwriteIfNecessary(Path newLocation, ExperimentProperties text) {
    if (Files.exists(newLocation)) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.titleProperty().bind(wrap(text.overwriteData()));
      alert.headerTextProperty().bind(wrap(text.overwriteDataConfirmation(newLocation)));

      boolean success = alert.showAndWait().map(ButtonType.OK::equals).orElse(false);

      if (success) {
        try {
          Files.walk(newLocation).sorted(reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
          throw new ExperimentException(
              format("Unable to delete existing data at %s", newLocation),
              e);
        }
      } else {
        throw new ExperimentException("Cancelled choose experiment name");
      }
    }
  }
}
