package uk.co.saiman.msapex.experiment;

import static uk.co.strangeskies.fx.FXUtilities.wrap;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentText;
import uk.co.strangeskies.eclipse.Localize;

/**
 * Toggle available size options for the periodic table
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
			okButton.setDisable(!newValue.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*"));
		});

		nameDialog.showAndWait().ifPresent(name -> {
			ExperimentConfiguration configuration = new ExperimentConfiguration() {
				@Override
				public void setNotes(String notes) {}

				@Override
				public void setName(String name) {}

				@Override
				public String getNotes() {
					return "";
				}

				@Override
				public String getName() {
					return name;
				}
			};

			experimentPart.getExperimentWorkspace().addRootExperiment(configuration);
			experimentPart.getExperimentTreeController().refresh();
		});
	}
}
