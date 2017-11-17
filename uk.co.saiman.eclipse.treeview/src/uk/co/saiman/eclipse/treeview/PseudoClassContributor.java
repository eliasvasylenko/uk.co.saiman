package uk.co.saiman.eclipse.treeview;

import static javafx.css.PseudoClass.getPseudoClass;

import javafx.css.PseudoClass;
import javafx.scene.Node;

public class PseudoClassContributor implements Contributor {
  private final PseudoClass pseudoClass;

  public PseudoClassContributor(String name) {
    this.pseudoClass = getPseudoClass(name);
  }

  @Override
  public Node configureCell(Node content) {
    content.pseudoClassStateChanged(pseudoClass, true);
    return content;
  }
}
