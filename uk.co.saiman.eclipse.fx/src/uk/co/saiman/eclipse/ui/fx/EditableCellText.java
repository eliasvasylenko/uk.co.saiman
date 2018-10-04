package uk.co.saiman.eclipse.ui.fx;

import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import uk.co.saiman.eclipse.model.ui.EditableCell;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.eclipse.ui.SaiUiModel;

@Creatable
public class EditableCellText extends StackPane {
  private final Label label = new Label();
  private final TextField field = new TextField();
  private Consumer<String> update;

  @Inject
  EditableCell cell;

  public EditableCellText() {
    StackPane.setAlignment(label, Pos.CENTER_LEFT);
    getChildren().add(label);
    getChildren().add(field);
    field.setOnAction(event -> {
      cell.setEditing(false);
      event.consume();
    });
  }

  @Inject
  public void editing(@Optional @UIEventTopic(SaiUiEvents.EditableCell.TOPIC_EDITING) Event event) {
    if (event != null && event.getProperty(UIEvents.EventTags.ELEMENT) != cell) {
      return;
    }

    label.setVisible(!cell.isEditing());
    field.setVisible(cell.isEditing());

    if (cell.isEditing()) {
      field.requestFocus();

    } else {
      if (cell.getTags().contains(SaiUiModel.EDIT_CANCELED)) {
        field.setText(label.getText());

      } else {
        label.setText(field.getText());
        if (update != null) {
          update.accept(field.getText());
        }
      }
    }
  }

  public void setText(String name) {
    label.setText(name);
    field.setText(name);
  }

  public void setUpdate(Consumer<String> update) {
    this.update = update;
  }

  public Label getLabel() {
    return label;
  }

  public TextField getTextField() {
    return field;
  }
}
