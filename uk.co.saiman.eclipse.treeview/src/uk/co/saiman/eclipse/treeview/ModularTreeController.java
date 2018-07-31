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
package uk.co.saiman.eclipse.treeview;

import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.fx.core.di.Service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import uk.co.saiman.eclipse.service.ObservableService;
import uk.co.saiman.eclipse.treeview.impl.Item;
import uk.co.saiman.eclipse.treeview.impl.ItemGroup;
import uk.co.saiman.eclipse.treeview.impl.ModularTreeCell;
import uk.co.saiman.eclipse.treeview.impl.TreeItemImpl;
import uk.co.saiman.eclipse.ui.FormatConverter;
import uk.co.saiman.eclipse.ui.model.MCell;

/**
 * A controller over a modular tree view for use within an Eclipse RCP
 * environment.
 * <p>
 * This class allows {@link MCell tree contributions} to be
 * contributed via {@link MCell contributors} so that the
 * contributions are instantiated according to an Eclipse injection context.
 * 
 * @author Elias N Vasylenko
 */
public class ModularTreeController<T> {
  private final StringProperty tableId = new SimpleStringProperty();

  @FXML
  private TreeView<Object> treeView;

  @Inject
  private IEclipseContext context;

  @Inject
  @Service
  private List<MCell> contributors;

  @Inject
  private ESelectionService selectionService;

  /**
   * Instantiate a controller with the default id - the simple name of the class -
   * and no contribution filter.
   */
  public ModularTreeController() {
    tableId.set(getClass().getName());
  }

  /**
   * @param id
   *          the {@link #getId() ID} of the controller to create
   */
  public ModularTreeController(String id) {
    tableId.set(id);
  }

  @FXML
  void initialize() {
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
   * @return The ID property of the controller. This is used to allow
   *         {@link MCell contributions} to filter which controllers
   *         they wish to contribute to.
   */
  public StringProperty getTableIdProperty() {
    return tableId;
  }

  /**
   * @return the current ID of the controller
   */
  public String getId() {
    return tableId.get();
  }

  /**
   * @param id
   *          the new ID for the controller
   */
  public void setId(String id) {
    tableId.set(id);
  }

  /**
   * @return the currently selected tree item
   */
  public TreeItem<Object> getSelection() {
    return treeView.getSelectionModel().getSelectedItem();
  }

  /**
   * @return the currently selected tree item data
   */
  public Object getSelectionData() {
    return getSelection().getValue();
  }

  @SuppressWarnings("unchecked")
  public T getRootData() {
    return (T) getRoot().getData();
  }

  /**
   * @param root
   *          the root object supplemented with its exact generic type
   */
  public void setRootData(T root) {
    TreeItemImpl<?> rootItem = createRoot(root);
    rootItem.setExpanded(true);
    treeView.setShowRoot(false);

    // add root
    treeView.setRoot(rootItem);

    treeView.refresh();
  }

  protected TreeItemImpl<?> createRoot(T root) {
    return new TreeItemImpl<>(new Item<T>() {
      @Override
      public T object() {
        return root;
      }

      @Override
      public void setObject(T object) {
        throw new UnsupportedOperationException();
      }

      @Override
      public ItemGroup<T> group() {
        return new ItemGroup<T>() {
          @Override
          public boolean isSettable() {
            return false;
          }

          @Override
          public Optional<String> contributionId() {
            // TODO Auto-generated method stub
            return null;
          }

          @Override
          public Optional<Object> anonymousContribution() {
            // TODO Auto-generated method stub
            return null;
          }

          @Override
          public Stream<FormatConverter<T>> formatConverters() {
            // TODO Auto-generated method stub
            return null;
          }

          @Override
          public Stream<DragHandler<T>> dragHandlers() {
            // TODO Auto-generated method stub
            return null;
          }

          @Override
          public Stream<DropHandler<T>> dropHandlers() {
            // TODO Auto-generated method stub
            return null;
          }
        };
      }
    }, context, this);
  }

  IEclipseContext getContext() {
    return context;
  }

  protected TreeItemImpl<?> getRoot() {
    return (TreeItemImpl<?>) treeView.getRoot();
  }

  public void refresh() {
    getRoot().requestRefresh();
  }

  public Stream<MCell> getContributors() {
    return contributors.stream();
  }
}
