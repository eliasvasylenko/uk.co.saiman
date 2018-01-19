package uk.co.saiman.eclipse.treeview;

public interface TreeEditor<T> {
  void addEditListener(Runnable onEdit);

  boolean isEditable();

  /**
   * Mark the tree item as editable and check if it is currently editing.
   * 
   * @return true if the item is editing, false otherwise
   */
  boolean isEditing();

  void commitEdit();
}
