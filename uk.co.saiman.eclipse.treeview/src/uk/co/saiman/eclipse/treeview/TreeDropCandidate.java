package uk.co.saiman.eclipse.treeview;

import java.util.stream.Stream;

public interface TreeDropCandidate<T> {
  Stream<T> data();

  TreeDropPosition position();

  /**
   * Get the tree entry adjacent to the drop position in the case of a
   * {@link TreeDropPosition#BEFORE_CHILD} or {@link TreeDropPosition#AFTER_CHILD}
   * drop position. Returns null in the case of {@link TreeDropPosition#OVER}.
   * 
   * @return the adjacent object or else null
   */
  TreeEntry<?> adjacentEntry();
}
