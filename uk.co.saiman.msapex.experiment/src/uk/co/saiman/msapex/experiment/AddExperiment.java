/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.strangeskies.fx.FXUtilities.wrap;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.strangeskies.eclipse.Localize;

/**
 * Add an experiment to the workspace
 * 
 * @author Elias N Vasylenko
 */
public class AddExperiment {
	/**
	 * The ID of the command in the e4 model fragment.
	 */
	public static final String COMMAND_ID = "uk.co.saiman.msapex.experiment.command.addexperiment";

	@Execute
	void execute(MPart part, @Localize ExperimentProperties text) {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();

		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.titleProperty().bind(wrap(text.newExperiment()));
		nameDialog.headerTextProperty().bind(wrap(text.newExperimentName()));

		Button okButton = (Button) nameDialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDisable(true);
		nameDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

			boolean exists = experimentPart.getExperimentWorkspace().getRootExperiments().stream()
					.anyMatch(e -> e.getState().getName().equals(newValue));

			boolean isValid = ExperimentConfiguration.isNameValid(newValue);

			okButton.setDisable(!isValid || exists);
		});

		nameDialog.showAndWait().ifPresent(name -> {
			experimentPart.getExperimentWorkspace().addRootExperiment(name);
			experimentPart.getExperimentTreeController().getTreeView().refresh();
		});
	}
}
