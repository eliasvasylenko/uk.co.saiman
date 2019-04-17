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

import static uk.co.saiman.fx.FxUtilities.wrap;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.workspace.Workspace;

/**
 * Add an experiment to the workspace
 * 
 * @author Elias N Vasylenko
 */
public class RenameExperimentDialog extends TextInputDialog {
  public RenameExperimentDialog(Workspace workspace, ExperimentProperties text, String name) {
    titleProperty().bind(wrap(text.renameExperiment()));
    headerTextProperty().bind(wrap(text.renameExperimentName(name)));

    GridPane content = (GridPane) getDialogPane().getContent();
    content.add(new Label("This is where we need to put format error messages..."), 0, 0);
    content.getChildren().remove(getEditor());
    content.add(getEditor(), 0, 1);

    Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
    okButton.setDisable(true);
    getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

      boolean exists = workspace.getWorkspaceExperiment(newValue).isPresent();

      boolean isValid = Procedure.isNameValid(newValue);

      okButton.setDisable(!isValid || exists);
    });
  }
}
