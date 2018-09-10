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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.ui.workbench.UIEvents.Handler;
import org.eclipse.e4.ui.workbench.UIEvents.HandlerContainer;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.fx.TransferCellHandler;
import uk.co.saiman.eclipse.ui.fx.TransferCellIn;
import uk.co.saiman.eclipse.ui.fx.TransferCellOut;

/**
 * Users should not need to extend this class. Item specific behavior should be
 * handled by extending {@link Cell} for each type of node which can appear in a
 * tree.
 * 
 * TODO Cells need a "context value" property to define which their main value
 * coming from the context is. This way we can know which object needs to have
 * the cells {@link Cell#getTransferFormats() transfer formats} applied to it
 * for drag/drop/copy/paste etc.
 * 
 * When we drag/copy it's easy to know what to do. Serialize the value of the
 * selected cell.
 * 
 * When we drop it's more difficult.
 * 
 * When we drop OVER a cell, find the first child the clipboard is compatible
 * with and for which the context value is modifiable, and set it (and set it to
 * be rendered if necessary).
 * 
 * Failing that, if the cell we drop over itself is compatible and modifiable
 * ... maybe set it or maybe do nothing.
 * 
 * What about dropping BEFORE or AFTER an item? same behavior as dropping over
 * the parent, or nothing. Can't really think of any useful way to apply the
 * drop target semantics.
 * 
 * What about dropping OVER an item with no existing compatible children? i.e. a
 * situation where we can add new children. How do we know what kind of child to
 * turn the clipboard data into? We could add some mechanism to the model itself
 * such as child prototypes / child contributions (in the manner of
 * menu-contributions) but the behavior would be very complex (much moreso than
 * menu-contributions) and it wouldn't nicely map to the nice
 * {@link ChildrenService} API. It may be simpler just to allow manual addition
 * of transfer handlers.
 * 
 * Should these also be considered part of the model api? Should they be FX
 * specific? Should they be accessed through the context?
 * 
 * How does this tie in with copy/paste {@link Handler handlers}? Add handlers
 * which just forward to this system, or use handlers as the mechanism to
 * implement the system?
 * 
 * TODO Cells need to be {@link HandlerContainer handler containers}.
 * 
 * @author Elias N Vasylenko
 */
public class TreeItemImpl extends TreeItem<Cell> implements IAdaptable {
  // current state
  private List<ItemList<?>> children;
  private boolean editable;

  // ui container
  private final BorderPane container;

  private final Map<Object, TreeItemImpl> childTreeItems = new HashMap<>();
  private boolean childrenCalculated;

  public TreeItemImpl(Cell domElement) {
    container = new BorderPane();
    setGraphic(container);
    setValue(domElement);
  }

  void setWidget(Pane widget) {
    container.setCenter(widget);
  }

  protected void updateValue() {
    fireSyntheticValueChangedEvent();
  }

  /**
   * Fire off an event to indicate that the value of the tree item has been
   * mutated, without actually changing the value property.
   */
  private void fireSyntheticValueChangedEvent() {
    Event.fireEvent(this, new TreeModificationEvent<>(valueChangedEvent(), this, getValue()));
  }

  @Override
  public ObservableList<TreeItem<Cell>> getChildren() {
    if (!childrenCalculated) {
      refreshContributions();
      rebuildChildren();
    }

    return super.getChildren();
  }

  public Stream<TreeItemImpl> getModularChildren() {
    return getChildren().stream().map(c -> (TreeItemImpl) c);
  }

  private void rebuildChildren() {
    List<TreeItem<Cell>> children;

    List<ItemList<?>> listItemGroups = this.children.stream().collect(toList());

    if (!listItemGroups.isEmpty()) {
      if (isExpanded()) {
        children = listItemGroups.stream().flatMap(this::rebuildChildrenGroup).collect(toList());

        childrenCalculated = true;
      } else {
        // So we get an arrow to expand without having to add the real children:
        children = Arrays.asList(new TreeItem<>());
        childrenCalculated = false;
      }
    } else {
      setExpanded(false);
      childTreeItems.clear();

      children = Collections.emptyList();
      childrenCalculated = true;
    }

    super.getChildren().setAll(children);
  }

  private Stream<TreeItem<Cell>> rebuildChildrenGroup(ItemList<?> group) {
    /*
     * TODO Also reuse the same TreeItemImpl for groups containing only a single
     * object...
     */
    childTreeItems.keySet().retainAll(group.getItems().map(Item::getObject).collect(toSet()));
    return group.getItems().map(this::rebuildChildItem);
  }

  private TreeItemImpl rebuildChildItem(Item<?> item) {
    System.out.println(item.getObject());

    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> U getAdapter(Class<U> adapter) {
    if (adapter == TreeItem.class) {
      return (U) this;
    }

    if (adapter.isAssignableFrom(Cell.class)) {
      return (U) getValue();
    }

    Object data = getValue().getObject();
    return data != null ? getValue().getContext().get(Adapter.class).adapt(data, adapter) : null;
  }

  boolean isEditable() {
    return editable;
  }

  public void editingStarted(Runnable cancel) {
    refreshContributions();
  }

  public void editingComplete() {
    refreshContributions();
  }

  public void editingCancelled() {
    refreshContributions();
  }

  private void refreshContributions() {
    System.out.println("refresh cell " + getValue().getLabel());
  }

  TransferCellOut transferOut() {
    return getValue().getContext().get(TransferCellHandler.class).transferOut(getValue());
  }

  TransferCellIn transferIn(Dragboard clipboard, TransferDestination position) {
    return getValue()
        .getContext()
        .get(TransferCellHandler.class)
        .transferIn(getValue(), clipboard, position);
  }
}
