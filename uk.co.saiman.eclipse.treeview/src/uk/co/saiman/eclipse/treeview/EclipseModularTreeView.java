package uk.co.saiman.eclipse.treeview;

import org.eclipse.e4.core.services.adapter.Adapter;

import uk.co.saiman.fx.ModularTreeView;
import uk.co.saiman.fx.TreeItemImpl;
import uk.co.saiman.reflection.token.TypedReference;

public class EclipseModularTreeView extends ModularTreeView {
  private Adapter adapter;

  public void setAdapter(Adapter adapter) {
    this.adapter = adapter;
  }

  public <T> T adapt(EclipseTreeItem<?>.EclipseTreeItemData data, Class<T> type) {
    return adapter.adapt(data.data(), type);
  }

  @Override
  protected TreeItemImpl<?> createRoot(TypedReference<?> root) {
    return new EclipseTreeItem<>(root, this);
  }
}
