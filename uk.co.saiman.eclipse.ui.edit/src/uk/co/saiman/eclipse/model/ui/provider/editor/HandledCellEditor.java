package uk.co.saiman.eclipse.model.ui.provider.editor;

public class HandledCellEditor extends CellEditor {
  @Override
  public String getLabel(Object element) {
    return getString("_UI_HandledCell_type");
  }

  @Override
  public String getDescription(Object element) {
    return getString("_UI_HandledCell_editor_description");
  }
}
