package uk.co.saiman.eclipse.ui.fx.impl;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.fx.ui.workbench.base.rendering.ElementRenderer;
import org.eclipse.fx.ui.workbench.renderers.fx.DefWorkbenchRendererFactory;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Tree;

public class ExtendedRendererFactory extends DefWorkbenchRendererFactory {
  private DefCellRenderer cellRenderer;
  private DefTreeRenderer treeRenderer;

  @Inject
  public ExtendedRendererFactory(IEclipseContext context) {
    super(context);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R extends ElementRenderer<?, ?>> R getRenderer(MUIElement modelObject) {
    if (modelObject instanceof Cell) {
      if (this.cellRenderer == null) {
        this.cellRenderer = make(getCellRendererClass());
      }
      return (R) this.cellRenderer;
    } else if (modelObject instanceof Tree) {
      if (this.treeRenderer == null) {
        this.treeRenderer = make(getTreeRendererClass());
      }
      return (R) this.treeRenderer;
    }
    return super.getRenderer(modelObject);
  }

  protected Class<DefCellRenderer> getCellRendererClass() {
    return DefCellRenderer.class;
  }

  protected Class<DefTreeRenderer> getTreeRendererClass() {
    return DefTreeRenderer.class;
  }
}
