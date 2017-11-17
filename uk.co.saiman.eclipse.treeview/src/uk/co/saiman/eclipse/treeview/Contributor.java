package uk.co.saiman.eclipse.treeview;

import javafx.scene.Node;

public interface Contributor {
  Node configureCell(Node content);
}
