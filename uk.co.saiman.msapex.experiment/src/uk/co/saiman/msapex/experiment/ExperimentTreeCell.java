package uk.co.saiman.msapex.experiment;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;

public class ExperimentTreeCell extends TreeCell<TreeItemData<?>> {
	@FXML
	private Node graphic;
	@FXML
	private Label name;
	@FXML
	private Label supplemental;

	public ExperimentTreeCell() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ExperimentTreeCell.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	protected void updateItem(TreeItemData<?> item, boolean empty) {
		updateItemCapture(item, empty);
	}

	protected <T> void updateItemCapture(TreeItemData<T> item, boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setGraphic(null);
		} else {
			setGraphic(graphic);

			name.setText(item.getItemType().getText(item.getData()));
			supplemental.setText(item.getItemType().getSupplementalText(item.getData()));
		}
	}
}
