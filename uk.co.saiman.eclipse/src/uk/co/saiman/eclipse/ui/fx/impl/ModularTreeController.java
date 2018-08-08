/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.treeview.
 *
 * uk.co.saiman.eclipse.treeview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.treeview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Collections.reverse;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import uk.co.saiman.eclipse.ui.fx.TreeService;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MTree;

/**
 * A controller over a modular tree view for use within an Eclipse RCP
 * environment.
 * <p>
 * This class allows {@link MCell tree contributions} to be contributed via
 * {@link MCell contributors} so that the contributions are instantiated
 * according to an Eclipse injection context.
 * 
 * @author Elias N Vasylenko
 */
public class ModularTreeController<T> {
  @FXML
  private String id;

  @FXML
  private TreeView<Object> treeView;

  @Inject
  private MTree tree;

  @Inject
  private IEclipseContext context;

  @FXML
  void initialize() {
    tree = context.get(MTree.class);
    if (tree == null) {
      TreeService treeService = context.get(TreeService.class);
      tree = treeService.getTree(id);
      context = tree.getContext();
    }

    ESelectionService selectionService = context.get(ESelectionService.class);

    treeView.setCellFactory(v -> new ModularTreeCell());
    treeView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          selectionService.setSelection(newValue);
        });

  }

  public void onKeyPressed(KeyEvent event) {
    switch (event.getCode()) {
    case DELETE:
      event.consume();
      List<TreeItem<?>> selection = new ArrayList<>(
          treeView.getSelectionModel().getSelectedItems());
      reverse(selection);
      for (TreeItem<?> treeItem : selection) {
        ((TreeItemImpl<?>) treeItem).delete();
      }
    default:
      break;
    }
  }

  public void onKeyReleased(KeyEvent event) {
    switch (event.getCode()) {
    case CONTEXT_MENU:
      event.consume();
      Node selectionBounds = treeView;

      Bounds sceneBounds = selectionBounds.localToScene(selectionBounds.getLayoutBounds());
      Bounds screenBounds = selectionBounds.localToScreen(selectionBounds.getLayoutBounds());

      PickResult pickResult = new PickResult(
          selectionBounds,
          sceneBounds.getMaxX(),
          sceneBounds.getMaxY());

      treeView
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

  @PreDestroy
  void destroy() {}

  /**
   * @return the currently selected tree item
   */
  public TreeItem<Object> getSelection() {
    return treeView.getSelectionModel().getSelectedItem();
  }

  public TreeView<Object> getTreeView() {
    return treeView;
  }

  /**
   * @return the currently selected tree item data
   */
  public Object getSelectionData() {
    return getSelection().getValue();
  }

  @SuppressWarnings("unchecked")
  public T getRootData() {
    return (T) treeView.getRoot().getValue();
  }

  /**
   * @param root
   *          the root object supplemented with its exact generic type
   */
  public void setRootData(T root) {
    TreeItem<Object> rootItem = createRoot(root);
    rootItem.setExpanded(true);
    treeView.setShowRoot(false);

    // add root
    treeView.setRoot(rootItem);

    treeView.refresh();
  }

  protected TreeItem<Object> createRoot(T root) {
    return new TreeItemImpl<T>(
        new ItemList<>(tree.getRootCell(), root).getItems().findFirst().get(),
        context,
        this);
  }

  IEclipseContext getContext() {
    return context;
  }
}
