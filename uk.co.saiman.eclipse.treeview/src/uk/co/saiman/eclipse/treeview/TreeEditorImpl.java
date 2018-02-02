package uk.co.saiman.eclipse.treeview;

final class TreeEditorImpl<T> implements TreeEditor<T> {
  private final Runnable commitEdit;
  private Runnable editListener;
  private boolean editable;

  TreeEditorImpl(Runnable commitEdit) {
    this.commitEdit = commitEdit;
    editable = commitEdit != null;
  }

  @Override
  public void addEditListener(Runnable onEdit) {
    if (editListener == null) {
      editListener = onEdit;
    } else {
      editListener = () -> {
        editListener.run();
        onEdit.run();
      };
    }
  }

  @Override
  public boolean isEditable() {
    return editable;
  }

  @Override
  public boolean isEditing() {
    editable = true;
    return commitEdit != null;
  }

  @Override
  public void commitEdit() {
    if (editListener != null)
      editListener.run();
    commitEdit.run();
  }
}
