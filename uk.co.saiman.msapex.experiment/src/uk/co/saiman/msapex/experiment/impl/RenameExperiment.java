/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.msapex.experiment.impl;

import static java.util.Comparator.reverseOrder;
import static uk.co.strangeskies.fx.FxUtilities.wrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.text.properties.Localized;

/**
 * Add an experiment to the workspace
 * 
 * @author Elias N Vasylenko
 */
public class RenameExperiment {
	/**
	 * The ID of the command in the e4 model fragment.
	 */
	public static final String COMMAND_ID = "uk.co.saiman.msapex.experiment.command.addexperiment";

	@Execute
	void execute(MPart part, @Localize ExperimentProperties text) {
		ExperimentPartImpl experimentPart = (ExperimentPartImpl) part.getObject();
		Object itemData = experimentPart.getExperimentTreeController().getSelectionData().data();

		if (!(itemData instanceof ExperimentNode<?, ?>
				&& ((ExperimentNode<?, ?>) itemData).getType() instanceof ExperimentRoot)) {
			throw new ExperimentException(
					text.exception().illegalCommandForSelection(COMMAND_ID, itemData));
		}

		@SuppressWarnings("unchecked")
		ExperimentNode<?, ExperimentConfiguration> selectedNode = (ExperimentNode<?, ExperimentConfiguration>) itemData;

		requestExperimentNameDialog(
				experimentPart,
				text.renameExperiment(),
				text.renameExperimentName(selectedNode.getState().getName())).ifPresent(name -> {
					Path newLocation = experimentPart.getExperimentWorkspace().getWorkspaceDataPath().resolve(
							name);

					RenameExperiment.confirmOverwriteIfNecessary(newLocation, text);

					selectedNode.getState().setName(name);
					experimentPart.getExperimentTreeController().getTreeView().refresh();
				});
	}

	static Optional<String> requestExperimentNameDialog(
			ExperimentPartImpl experimentPart,
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

			boolean exists = experimentPart.getExperimentWorkspace().getExperiments().anyMatch(
					e -> e.getState().getName().equals(newValue));

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
					throw new ExperimentException(text.exception().cannotDelete(newLocation), e);
				}
			} else {
				throw new ExperimentException(text.exception().userCancelledSetExperimentName());
			}
		}
	}
}
