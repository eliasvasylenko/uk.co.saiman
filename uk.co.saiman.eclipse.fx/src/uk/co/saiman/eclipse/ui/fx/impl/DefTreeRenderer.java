/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Collections.reverse;
import static uk.co.saiman.eclipse.ui.TransferMode.DISCARD;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.fx.ui.workbench.renderers.base.BaseRenderer;
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
import uk.co.saiman.eclipse.ui.SaiUiEvents;
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
    public TreeImpl(@Named(BaseRenderer.CONTEXT_DOM_ELEMENT) Tree domElement) {
      setDomElement(domElement);
    }

    @Inject
    public void setEditable(@Named(SaiUiEvents.Tree.EDITABLE) boolean editable) {
      getWidget().setEditable(true);
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

    @Override
    public void setStyleId(String id) {
      getWidget().setId(id);
    }

    @Override
    protected TreeView<Cell> createWidget() {
      TreeView<Cell> tree = new TreeView<>();

      tree.setCellFactory(v -> new TreeCellImpl());
      tree.setRoot(new TreeItem<>(null));
      tree.setShowRoot(false);
      tree.setOnKeyPressed(this::onKeyPressed);
      tree.setOnKeyReleased(this::onKeyReleased);

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
      getWidget().getRoot().getChildren().remove(((CellImpl) widget).getTreeItem());
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
      TreeItem<Cell> selectedItem = getWidget().getSelectionModel().getSelectedItem();

      if (selectedItem == null)
        return;

      CellImpl target = ((CellImpl) selectedItem.getValue().getWidget());
      Node widget = target.getWidget();

      switch (event.getCode()) {
      case CONTEXT_MENU:
        event.consume();

        Bounds sceneBounds = widget.localToScene(widget.getLayoutBounds());
        Bounds screenBounds = widget.localToScreen(widget.getLayoutBounds());

        PickResult pickResult = new PickResult(
            widget,
            sceneBounds.getMaxX(),
            sceneBounds.getMaxY());

        widget
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

      case ENTER:
        if (target.executeAction()) {
          event.consume();
        }
        break;

      default:
        break;
      }
    }
  }

  @Override
  protected void initWidget(Tree tree, WTree<TreeView<Cell>> widget) {
    super.initWidget(tree, widget);

    MPart part = tree.getContext().get(MPart.class);

    if (part != null) {
      ESelectionService selectionService = part.getContext().get(ESelectionService.class);

      widget
          .getWidget()
          .getSelectionModel()
          .selectedItemProperty()
          .addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
              newValue.getValue().getContext().activateBranch();
              selectionService.setSelection(((TreeItemImpl) newValue).getData());
            } else {
              selectionService.setSelection(null);
            }
          });
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
