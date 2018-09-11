package uk.co.saiman.eclipse.ui.fx.impl;

import static javafx.geometry.Pos.CENTER_LEFT;
import static org.eclipse.e4.core.contexts.ContextInjectionFactory.invoke;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.commands.ExpressionContext;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.emf.common.util.URI;
import org.eclipse.fx.core.log.Log;
import org.eclipse.fx.core.log.Logger;
import org.eclipse.fx.ui.services.resources.GraphicsLoader;
import org.eclipse.fx.ui.workbench.fx.EMFUri;
import org.eclipse.fx.ui.workbench.renderers.base.BaseRenderer;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WPopupMenu;
import org.eclipse.fx.ui.workbench.renderers.fx.internal.CustomContainerSupport;
import org.eclipse.fx.ui.workbench.renderers.fx.widget.WWidgetImpl;

import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.HandledCell;
import uk.co.saiman.eclipse.ui.fx.TreeService;
import uk.co.saiman.eclipse.ui.fx.widget.WCell;
import uk.co.saiman.function.ThrowingConsumer;

/**
 * default renderer for {@link Cell}
 */
public class DefCellRenderer extends BaseCellRenderer<Pane> {
  public static class CellImpl extends WWidgetImpl<Pane, Cell> implements WCell<Pane> {
    @Inject
    @Log
    Logger logger;

    @Inject
    GraphicsLoader graphicsLoader;

    @Inject
    IEclipseContext context;

    private Label label;

    private final TreeItemImpl treeItem;

    private ContextMenu popupMenu;

    @Inject
    public CellImpl(@Named(BaseRenderer.CONTEXT_DOM_ELEMENT) Cell domElement) {
      this.treeItem = new TreeItemImpl(domElement);
      setDomElement(domElement);
    }

    public TreeItem<Cell> getTreeItem() {
      return treeItem;
    }

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

    @Inject
    public void setLabel(@Named(UIEvents.UILabel.LOCALIZED_LABEL) String label) {
      getWidget();
      this.label.setText(label);
    }

    @Inject
    public void setIconURI(@Named(UIEvents.UILabel.ICONURI) String uri) {
      getWidget();
      if (uri == null || uri.isEmpty()) {
        this.label.setGraphic(null);
      } else {
        this.label.setGraphic(this.graphicsLoader.getGraphicsNode(new EMFUri(URI.createURI(uri))));
      }
    }

    @Override
    public void setStyleId(String id) {
      getWidget().setId(id);
    }

    @Override
    protected Pane createWidget() {
      Pane node = CustomContainerSupport.createContainerPane(this.logger, context);
      node = node == null ? new HBox() : node;

      Label label = new Label();
      label.setId(TreeService.TEXT_ID);
      node.getChildren().add(label);
      HBox.setHgrow(label, Priority.ALWAYS);

      node.setPrefWidth(0);

      if (node instanceof HBox) {
        ((HBox) node).setAlignment(CENTER_LEFT);
      }

      this.label = label;

      treeItem.setWidget(node);

      node.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
        showContextMenu(event.getScreenX(), event.getScreenY());
        event.consume();
      });
      node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
        hideContextMenu();
      });

      return node;
    }

    @Override
    protected void setUserData(WWidgetImpl<Pane, Cell> widget) {
      getWidget().setUserData(widget);
    }

    @Override
    public void setOnActionCallback(Runnable runnable) {
      // TODO Auto-generated method stub

    }

    @Override
    public void addCell(WCell<?> widget) {
      getTreeItem().getChildren().add(((CellImpl) widget).getTreeItem());
    }

    @Override
    public void addCell(int idx, WCell<?> widget) {
      getTreeItem().getChildren().add(idx, ((CellImpl) widget).getTreeItem());
    }

    @Override
    public void removeCell(WCell<?> widget) {
      getTreeItem().getChildren().remove(((CellImpl) widget).getTreeItem());
    }

    private void contributeCommand(HBox node, Cell model) {
      if (model instanceof HandledCell) {
        HandledCell handledModel = (HandledCell) model;
        if (handledModel.getWbCommand() != null) {
          contributeAction(
              node,
              context -> handledModel
                  .getWbCommand()
                  .executeWithChecks(
                      context.get(InputEvent.class),
                      new ExpressionContext(context)));
        }
      } else {
        contributeAction(node, context -> invoke(model.getObject(), Execute.class, context));
      }
    }

    private <E extends Exception> void contributeAction(
        HBox node,
        ThrowingConsumer<IEclipseContext, E> action) {
      node.addEventHandler(MouseEvent.ANY, event -> {
        if (event.getClickCount() == 2
            && event.getButton().equals(MouseButton.PRIMARY)
            && event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
          executeCommand(event, action);
        }
      });

      node.addEventHandler(KeyEvent.ANY, event -> {
        if (event.getCode() == KeyCode.ENTER) {
          if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
            executeCommand(event, action);
          }
        }
      });
    }

    private <E extends Exception> void executeCommand(
        InputEvent event,
        ThrowingConsumer<IEclipseContext, E> action) {
      IEclipseContext context = this.context.createChild();

      context.set(InputEvent.class, event);

      try {
        action.accept(context);
      } catch (InjectionException e) {
        // TODO log or discard?
      } catch (Exception t) {
        // TODO log
      }
    }

    @Override
    public void setPopupMenu(WPopupMenu<?> widget) {
      popupMenu = (ContextMenu) widget.getWidget();
      popupMenu.addEventHandler(KeyEvent.ANY, Event::consume);
    }

    private void showContextMenu(double screenX, double screenY) {
      if (popupMenu != null) {
        popupMenu.show(getWidget(), screenX, screenY);
      }
    }

    private void hideContextMenu() {
      if (popupMenu != null) {
        popupMenu.hide();
      }
    }
  }

  @Override
  protected Class<? extends WCell<Pane>> getWidgetClass(Cell element) {
    return CellImpl.class;
  }
}
