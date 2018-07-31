package uk.co.saiman.eclipse.ui.model;

import org.eclipse.e4.core.di.annotations.Execute;

public interface MTable {
  String getElementId();

  MCell getRootCell();

  /**
   * An editable tree contribution is modally editable and should be
   * {@link Execute re-executed upon entering or exiting the editing mode}. A tree
   * contribution may be modifiable without being editable.
   * 
   * @return true if this contribution implements an editing mode for an item it
   *         is applied to
   */
  boolean isEditable();

  void setEditable(boolean editable);

  Class<?> getContributionClass();
}
