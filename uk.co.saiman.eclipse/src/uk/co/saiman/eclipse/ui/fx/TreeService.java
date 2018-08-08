package uk.co.saiman.eclipse.ui.fx;

import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.ui.model.MTree;

public interface TreeService {
  String TEXT_ID = "text";
  String SUPPLEMENTAL_TEXT_ID = "supplementalText";

  TreeView<?> createTree(MTree treeModel, Object root);

  default TreeView<?> createTree(String treeModelId, Object root) {
    return createTree(getTree(treeModelId), root);
  }

  MTree getTree(String treeModelId);

  static void setLabel(HBox node, String text) {
    ((Label) node.lookup("#" + TEXT_ID)).setText(text);
  }

  static void setSupplemental(HBox node, String text) {
    ((Label) node.lookup("#" + SUPPLEMENTAL_TEXT_ID)).setText(text);
  }
}
