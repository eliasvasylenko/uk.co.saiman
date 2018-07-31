package uk.co.saiman.eclipse.ui.fx;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.ui.model.MTable;

public interface TableService {
  String TEXT_ID = "text";
  String SUPPLEMENTAL_TEXT_ID = "supplementalText";

  TableView<?> getTable(MTable tableModel, Object root);

  static void setLabel(HBox node, String text) {
    ((Label) node.lookup("#" + TEXT_ID)).setText(text);
  }

  static void setSupplemental(HBox node, String text) {
    ((Label) node.lookup("#" + SUPPLEMENTAL_TEXT_ID)).setText(text);
  }
}
