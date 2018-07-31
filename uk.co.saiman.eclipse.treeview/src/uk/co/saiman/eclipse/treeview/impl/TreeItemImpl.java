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
package uk.co.saiman.eclipse.treeview.impl;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javafx.geometry.Pos.CENTER_LEFT;
import static uk.co.saiman.eclipse.ui.TransferMode.DISCARD;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.adapter.Adapter;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.treeview.ModularTreeController;
import uk.co.saiman.eclipse.ui.TransferInPosition;
import uk.co.saiman.eclipse.ui.fx.TableService;
import uk.co.saiman.eclipse.ui.model.MCell;

/**
 * Users should not need to extend this class. Item specific behavior should be
 * handled by extending {@link MCell} for each type of node which can appear in
 * a tree.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of data for this tree item
 */
public class TreeItemImpl<T> extends TreeItem<Object> implements IAdaptable {
  private Item<T> item;
  private boolean editing;

  private final IEclipseContext context;

  private final ModularTreeController<?> controller;
  private final TreeItemImpl<?> parent;
  private final BorderPane container;

  private final Map<Object, TreeItemImpl<?>> childTreeItems = new HashMap<>();
  private boolean childrenCalculated;
  private CellSpecializationAggregator contributionsAggregator;

  public TreeItemImpl(Item<T> item, IEclipseContext context, ModularTreeController<?> controller) {
    this(item, context, controller, null);
  }

  public TreeItemImpl(Item<T> item, IEclipseContext context, TreeItemImpl<?> parent) {
    this(item, context, parent.controller, parent);
  }

  private TreeItemImpl(
      Item<T> item,
      IEclipseContext context,
      ModularTreeController<?> controller,
      TreeItemImpl<?> parent) {
    this.item = item;
    this.context = context;
    this.controller = controller;
    this.parent = parent;

    container = new BorderPane();
    setGraphic(container);

    setValue(getData());

    refresh();
  }

  public T getData() {
    return item.object();
  }

  private void updateValue() {
    fireSyntheticValueChangedEvent();
    requestRefresh();
  }

  /**
   * Fire off an event to indicate that the value of the tree item has been
   * mutated, without actually changing the value property.
   */
  private void fireSyntheticValueChangedEvent() {
    Event.fireEvent(this, new TreeModificationEvent<>(valueChangedEvent(), this, getValue()));
  }

  private void updateValue(T data) {
    if (!item.group().isSettable()) {
      throw new UnsupportedOperationException();
    }
    if (this.getData() != data) {
      setValue(data);
    } else {
      fireSyntheticValueChangedEvent();
    }
    item.setObject(data);
    requestRefresh();
  }

  @Override
  public ObservableList<TreeItem<Object>> getChildren() {
    if (!childrenCalculated) {
      refreshContributions();
      rebuildChildren();
    }

    return super.getChildren();
  }

  public Optional<TreeItemImpl<?>> getModularParent() {
    return Optional.ofNullable(parent);
  }

  public Stream<TreeItemImpl<?>> getModularChildren() {
    return getChildren().stream().map(c -> (TreeItemImpl<?>) c);
  }

  public void requestRefresh() {
    Platform.runLater(() -> {
      refresh();
    });
  }

  private void refresh() {
    boolean selected = false; // TODO
    boolean focused = false; // TODO

    refreshContributions();
    rebuildChildren();

    if (selected || focused) {
      // TODO reselect and focus
    }
  }

  private void rebuildChildren() {
    List<TreeItem<Object>> children;

    List<ListItemConfigurationImpl<?>> listItemGroups = contributionsAggregator
        .getChildren()
        .collect(toList());

    if (listItemGroups.isEmpty()) {
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

  private Stream<TreeItem<Object>> rebuildChildrenGroup(ListItemConfigurationImpl<?> group) {
    childTreeItems.keySet().retainAll(group.items().map(Item::object).collect(toSet()));
    return group.items().map(this::rebuildChildItem);
  }

  private TreeItemImpl<?> rebuildChildItem(Item<?> item) {
    Object object = item.object();
    TreeItemImpl<?> treeItem = childTreeItems.get(object);

    if (treeItem == null) {
      treeItem = new TreeItemImpl<>(item, context, this);
      childTreeItems.put(object, treeItem);
    } else {
      treeItem.refresh();
    }

    return treeItem;
  }

  void setEditing(boolean editing) {
    this.editing = true;
  }

  public boolean isEditing() {
    return editing;
  }

  void refreshContributions() {
    HBox node = createNode();

    container.setCenter(node);

    contributionsAggregator = new CellSpecializationAggregator(context, getData(), node);

    controller.getContributors().forEach(contributionsAggregator::inject);
  }

  private HBox createNode() {
    HBox node = new HBox();

    Label text = new Label(getData().toString());
    text.setId(TableService.TEXT_ID);
    node.getChildren().add(text);
    HBox.setHgrow(text, Priority.ALWAYS);

    Label supplemental = new Label();
    supplemental.setId(TableService.SUPPLEMENTAL_TEXT_ID);
    node.getChildren().add(supplemental);
    HBox.setHgrow(supplemental, Priority.SOMETIMES);

    node.setPrefWidth(0);
    node.setAlignment(CENTER_LEFT);

    return node;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> U getAdapter(Class<U> adapter) {
    if (adapter == TreeItem.class) {
      return (U) this;
    }

    T data = getData();
    if (adapter.isAssignableFrom(data.getClass())) {
      return (U) data;
    }

    return context.get(Adapter.class).adapt(data, adapter);
  }

  boolean isEditable() {
    return contributionsAggregator.isEditable();
  }

  public void delete() {
    TransferOutImpl<?> dragCandidate = getDragCandidate();
    if (dragCandidate.getTransferModes().contains(DISCARD)) {
      dragCandidate.handleDrag(DISCARD);
      parent.requestRefresh();
    }
  }

  TransferOutImpl<?> getDragCandidate() {
    return new TransferOutImpl<>(item);
  }

  TransfersIn getDropCandidates(Clipboard clipboard, TransferInPosition position) {
    if (position == TransferInPosition.OVER) {
      return new TransfersIn(
          contributionsAggregator
              .getChildren()
              .map(ListItemConfigurationImpl::itemGroup)
              .collect(toList()),
          clipboard,
          position,
          null);
    } else {
      return new TransfersIn(singletonList(item.group()), clipboard, position, getData());
    }
  }
}
