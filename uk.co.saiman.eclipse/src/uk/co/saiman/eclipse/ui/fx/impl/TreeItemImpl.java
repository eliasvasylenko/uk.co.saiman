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

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javafx.geometry.Pos.CENTER_LEFT;
import static org.eclipse.e4.core.contexts.ContextInjectionFactory.invoke;
import static uk.co.saiman.eclipse.ui.TransferMode.DISCARD;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.commands.ExpressionContext;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WPopupMenu;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.TreeService;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MHandledCell;
import uk.co.saiman.function.ThrowingConsumer;

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
  // current state
  private Item<T> item;
  private ListItemsImpl children;
  private boolean editable;
  private boolean editing;

  // ui container
  private final BorderPane container;

  // owner and context
  private final IEclipseContext context;
  private final ModularTreeController<?> controller;
  private final TreeItemImpl<?> parent;

  private final Map<Object, TreeItemImpl<?>> childTreeItems = new HashMap<>();
  private boolean childrenCalculated;

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
    return item.getObject();
  }

  public MCell getModel() {
    return item.getModel();
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
    Consumer<? super T> update = item
        .getUpdate()
        .orElseThrow(() -> new UnsupportedOperationException());

    if (this.getData() != data) {
      setValue(data);
    } else {
      fireSyntheticValueChangedEvent();
    }
    update.accept(data);
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

  void refreshContributions() {
    IEclipseContext context = this.context.createChild();
    try {
      HBox node = createNode();
      this.container.setCenter(node);
      context.set(HBox.class, node);

      ListItemsImpl children = new ListItemsImpl(getModel().getChildren());
      this.children = children;
      context.set(ListItems.class, children);

      getContributors().forEach(contributor -> contribute(node, contributor));
    } finally {
      context.dispose();
    }
  }

  private Stream<MCell> getContributors() {
    // TODO specialized cells first, then specializations, taking care if they are
    // exclusive. Or are they all exclusive?
    return Stream.of(getModel());
  }

  public void contribute(HBox node, MCell model) {
    ContextInjectionFactory.invoke(model.getObject(), AboutToShow.class, context);
    contributeCommand(node, model);
    contributePopupMenu(node, model);
  }

  private void contributeCommand(HBox node, MCell model) {
    if (model instanceof MHandledCell) {
      MHandledCell handledModel = (MHandledCell) model;
      if (handledModel.getWbCommand() != null) {
        contributeAction(
            node,
            context -> handledModel
                .getWbCommand()
                .executeWithChecks(context.get(InputEvent.class), new ExpressionContext(context)));
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
    } finally {
      context.dispose();
    }
  }

  private void contributePopupMenu(HBox node, MCell model) {
    Control menuControl = new Control() {};
    ContextMenu contextMenu = registerMenu(menuControl, model.getPopupMenu());
    contextMenu.addEventHandler(KeyEvent.ANY, Event::consume);

    node.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
      contextMenu.show(node, event.getScreenX(), event.getScreenY());
      event.consume();
    });
    node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      contextMenu.hide();
    });
  }

  @SuppressWarnings("unchecked")
  private ContextMenu registerMenu(Control widget, MPopupMenu menu) {
    if (menu.getWidget() != null) {
      WPopupMenu<ContextMenu> c = (WPopupMenu<ContextMenu>) menu.getWidget();
      return (ContextMenu) c.getWidget();
    }

    IPresentationEngine engine = context.get(IPresentationEngine.class);
    return (ContextMenu) ((WPopupMenu<ContextMenu>) engine.createGui(menu)).getWidget();
  }

  private void rebuildChildren() {
    List<TreeItem<Object>> children;

    List<ItemList<?>> listItemGroups = this.children.getItems().collect(toList());

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

  private Stream<TreeItem<Object>> rebuildChildrenGroup(ItemList<?> group) {
    /*
     * TODO Also reuse the same TreeItemImpl for groups containing only a single
     * object...
     */
    childTreeItems.keySet().retainAll(group.getItems().map(Item::getObject).collect(toSet()));
    return group.getItems().map(this::rebuildChildItem);
  }

  private TreeItemImpl<?> rebuildChildItem(Item<?> item) {
    Object object = item.getObject();
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

  private HBox createNode() {
    HBox node = new HBox();

    Label text = new Label(getData().toString());
    text.setId(TreeService.TEXT_ID);
    node.getChildren().add(text);
    HBox.setHgrow(text, Priority.ALWAYS);

    Label supplemental = new Label();
    supplemental.setId(TreeService.SUPPLEMENTAL_TEXT_ID);
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
    return editable;
  }

  public void delete() {
    TransferOut<?> dragCandidate = getDragCandidate();
    if (dragCandidate.getTransferModes().contains(DISCARD)) {
      dragCandidate.handleDrag(DISCARD);
      parent.requestRefresh();
    }
  }

  TransferOut<?> getDragCandidate() {
    return new TransferOut<>(item);
  }

  TransfersIn getDropCandidates(Dragboard clipboard, TransferDestination position) {
    if (position == TransferDestination.OVER) {
      return new TransfersIn(
          children.getItems().collect(toList()),
          clipboard,
          context.get(ClipboardService.class),
          position,
          null);
    } else {
      return new TransfersIn(
          singletonList(item.getList()),
          clipboard,
          context.get(ClipboardService.class),
          position,
          item);
    }
  }
}
