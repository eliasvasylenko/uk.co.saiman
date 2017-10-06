package uk.co.saiman.eclipse.treeview;

import org.eclipse.core.runtime.IAdaptable;

import uk.co.saiman.fx.ModularTreeView;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeItemImpl;
import uk.co.saiman.reflection.token.TypedReference;

public class EclipseTreeItem<T> extends TreeItemImpl<T> implements IAdaptable {
  protected EclipseTreeItem(TypedReference<T> data, ModularTreeView treeView) {
    super(data, treeView);
  }

  protected EclipseTreeItem(TypedReference<T> data, TreeItemImpl<?> parent) {
    super(data, parent);
  }

  @Override
  protected TreeItemData<?> createData(TypedReference<T> data) {
    return new EclipseTreeItemData(data);
  }

  @Override
  protected TreeItemImpl<?> createChild(TypedReference<?> data) {
    return new EclipseTreeItem<>(data, this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> U getAdapter(Class<U> adapter) {
    if (adapter == TreeItemData.class) {
      return (U) getData();
    }

    return getDataImpl().getAdapter(adapter);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected EclipseTreeItemData getDataImpl() {
    return (EclipseTreeItemData) super.getDataImpl();
  }

  public class EclipseTreeItemData extends TreeItemDataImpl implements IAdaptable {
    public EclipseTreeItemData(TypedReference<T> data) {
      super(data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> U getAdapter(Class<U> adapter) {
      if (adapter == type().getErasedType()) {
        return (U) data();
      }

      return treeView().adapt(this, adapter);
    }

    @Override
    public EclipseModularTreeView treeView() {
      return (EclipseModularTreeView) super.treeView();
    }
  }
}
