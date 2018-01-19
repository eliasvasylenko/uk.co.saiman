package uk.co.saiman.eclipse.treeview;

import static javafx.scene.layout.HBox.setHgrow;

import java.util.function.Consumer;

import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;

public class EditingTextField extends TextField {
  public EditingTextField(
      String value,
      TreeEditor<?> editor,
      Consumer<EditingTextField> setParent) {
    super(value);

    setOnAction(event -> {
      editor.commitEdit();
      event.consume();
    });
    setParent.accept(this);
    selectAll();
    requestFocus();

    setHgrow(this, Priority.SOMETIMES);
  }
}
