package uk.co.saiman.msapex.experiment;

import static uk.co.strangeskies.fx.FXUtilities.wrap;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import uk.co.saiman.experiment.ExperimentText;
import uk.co.strangeskies.eclipse.Localize;

/**
 * Add an experiment to the workspace
 * 
 * @author Elias N Vasylenko
 */
public class AddExperiment {
	private static final String ALPHANUMERIC = "[a-zA-Z0-9]+";
	private static final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

	@Execute
	void execute(MPart part, @Localize ExperimentText text) {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();

		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.titleProperty().bind(wrap(text.newExperiment()));
		nameDialog.headerTextProperty().bind(wrap(text.newExperimentName()));

		Button okButton = (Button) nameDialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDisable(true);
		nameDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			boolean exists = experimentPart.getExperimentWorkspace().getRootExperiments().stream()
					.anyMatch(e -> e.getState().getName().equals(newValue));
			boolean isValid = newValue.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*");
			okButton.setDisable(!isValid || exists);
		});

		nameDialog.showAndWait().ifPresent(name -> {
			experimentPart.getExperimentWorkspace().addRootExperiment(name);
			experimentPart.getExperimentTreeController().refresh();
		});
	}
}
