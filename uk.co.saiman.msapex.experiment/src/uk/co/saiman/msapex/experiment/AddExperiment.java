package uk.co.saiman.msapex.experiment;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import uk.co.saiman.experiment.ExperimentConfiguration;

/**
 * Toggle available size options for the periodic table
 * 
 * @author Elias N Vasylenko
 */
public class AddExperiment {
	private static final String NEW_EXPERIMENT = "New experiment";
	private static final String NEW_EXPERIMENT_NAME = "Please enter a new experiment name";
	private static final String ALPHANUMERIC = "[a-zA-Z0-9]+";
	private static final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

	@Execute
	void execute(EPartService partService) {
		ExperimentPart experimentPart = (ExperimentPart) partService.findPart(ExperimentPart.PART_ID).getObject();

		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.setTitle(NEW_EXPERIMENT);
		nameDialog.setHeaderText(NEW_EXPERIMENT_NAME);

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
