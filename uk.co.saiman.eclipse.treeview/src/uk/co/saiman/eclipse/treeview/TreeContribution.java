package uk.co.saiman.eclipse.treeview;

import static javafx.css.PseudoClass.getPseudoClass;

import javafx.scene.Node;

/**
 * This is a marker interface for OSGi services to signify an injectable
 * {@link ModularTreeView} contribution. Services with higher service rankings
 * are applied later so that they can choose to override other contributions.
 * <p>
 * Implementations which have fields injected should be registered as prototype
 * scope services, as if an instance is shared each tree will re-inject from
 * their own context.
 * <P>
 * TODO If this system is eventually migrated to an e4 model based definition,
 * this will probably be deprecated. It's function is only to allow OSGi-DS to
 * wire up the contributions to trees in the meantime. In such a hypothetical
 * system, ordering between contributions would probably be achieved by some
 * sort of "before:id" "after:id" type system as used by many other types of
 * model element, instead of service ranking.
 * 
 * @author Elias N Vasylenko
 */
public interface TreeContribution {
  default void configurePseudoClass(Node node) {
    configurePseudoClass(node, getClass().getSimpleName());
  }

  default void configurePseudoClass(Node node, String name) {
    node.pseudoClassStateChanged(getPseudoClass(name), true);
  }
}
