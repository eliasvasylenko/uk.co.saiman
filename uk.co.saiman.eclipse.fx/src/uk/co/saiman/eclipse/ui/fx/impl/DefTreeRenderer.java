package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Collections.reverse;
import static uk.co.saiman.eclipse.ui.TransferMode.DISCARD;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.fx.ui.workbench.renderers.fx.widget.WWidgetImpl;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Tree;
import uk.co.saiman.eclipse.ui.fx.TransferCellHandler;
import uk.co.saiman.eclipse.ui.fx.TransferCellOut;
import uk.co.saiman.eclipse.ui.fx.impl.DefCellRenderer.CellImpl;
import uk.co.saiman.eclipse.ui.fx.widget.WCell;
import uk.co.saiman.eclipse.ui.fx.widget.WTree;

/**
 * default renderer for {@link Tree}
 */
public class DefTreeRenderer extends BaseTreeRenderer<TreeView<Cell>> {
  public static class TreeImpl extends WWidgetImpl<TreeView<Cell>, Tree>
      implements WTree<TreeView<Cell>> {
    @Inject
    private ESelectionService selectionService;

    @Override
    public void addStyleClasses(List<String> classnames) {
      getWidget().getStyleClass().addAll(classnames);
    }

    @Override
    public void addStyleClasses(String... classnames) {
      getWidget().getStyleClass().addAll(classnames);
    }

    @Override
    public void removeStyleClasses(List<String> classnames) {
      getWidget().getStyleClass().removeAll(classnames);
    }

    @Override
    public void removeStyleClasses(String... classnames) {
      getWidget().getStyleClass().removeAll(classnames);
    }

    @Override
    public void setStyleId(String id) {
      getWidget().setId(id);
    }

    @Override
    protected TreeView<Cell> createWidget() {
      TreeView<Cell> tree = new TreeView<>();

      tree.setCellFactory(v -> new TreeCellImpl());
      tree
          .getSelectionModel()
          .selectedItemProperty()
          .addListener((observable, oldValue, newValue) -> {
            selectionService.setSelection(newValue);
          });
      tree.setRoot(new TreeItem<>(null));
      tree.setShowRoot(false);

      return tree;
    }

    @Override
    protected void setUserData(WWidgetImpl<TreeView<Cell>, Tree> widget) {
      getWidget().setUserData(widget);
    }

    @Override
    public void addCell(WCell<?> widget) {
      getWidget().getRoot().getChildren().add(((CellImpl) widget).getTreeItem());
    }

    @Override
    public void addCell(int idx, WCell<?> widget) {
      getWidget().getRoot().getChildren().add(idx, ((CellImpl) widget).getTreeItem());
    }

    @Override
    public void removeCell(WCell<?> widget) {
      getWidget().getRoot().getChildren().remove(widget.getWidget());
    }

    public void onKeyPressed(KeyEvent event) {
      switch (event.getCode()) {
      case DELETE:
        event.consume();
        List<TreeItem<?>> selection = new ArrayList<>(
            getWidget().getSelectionModel().getSelectedItems());
        reverse(selection);
        for (TreeItem<?> treeItem : selection) {
          TransferCellOut transfer = ((TreeItemImpl) treeItem).transferOut();
          if (transfer.supportedTransferModes().contains(DISCARD)) {
            transfer.handle(DISCARD);
          }
        }
      default:
        break;
      }
    }

    public void onKeyReleased(KeyEvent event) {
      switch (event.getCode()) {
      case CONTEXT_MENU:
        event.consume();
        Node selectionBounds = getWidget();

        Bounds sceneBounds = selectionBounds.localToScene(selectionBounds.getLayoutBounds());
        Bounds screenBounds = selectionBounds.localToScreen(selectionBounds.getLayoutBounds());

        PickResult pickResult = new PickResult(
            selectionBounds,
            sceneBounds.getMaxX(),
            sceneBounds.getMaxY());

        getWidget()
            .fireEvent(
                new ContextMenuEvent(
                    ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                    sceneBounds.getMaxX(),
                    sceneBounds.getMaxY(),
                    screenBounds.getMaxX(),
                    screenBounds.getMaxY(),
                    true,
                    pickResult));
        break;
      default:
        break;
      }
    }

    public TreeItem<Cell> getSelection() {
      return getWidget().getSelectionModel().getSelectedItem();
    }
  }

  @Override
  protected void doProcessContent(Tree element) {
    element
        .getContext()
        .set(
            TransferCellHandler.class,
            ContextInjectionFactory.make(DefaultTransferCellHandler.class, element.getContext()));
    super.doProcessContent(element);
  }

  @Override
  protected Class<? extends WTree<TreeView<Cell>>> getWidgetClass(Tree element) {
    return TreeImpl.class;
  }
}
