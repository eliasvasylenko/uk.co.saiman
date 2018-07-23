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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.treeview.TreeTransferMode.DISCARD;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.adapter.Adapter;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.ModularTreeController;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeDropPosition;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.eclipse.treeview.TreeEntryChild;
import uk.co.saiman.eclipse.treeview.impl.TreeClipboardManager.TreeDragCandidateImpl;
import uk.co.saiman.eclipse.treeview.impl.TreeClipboardManager.TreeDropCandidates;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * Users should not need to extend this class. Item specific behavior should be
 * handled by extending {@link TreeContribution} for each type of node which can
 * appear in a tree.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of data for this tree item
 */
public class ModularTreeItem<T> extends TreeItem<TreeEntry<?>> implements IAdaptable {
  private final IEclipseContext context;

  private final TypeToken<T> type;
  private final ModularTreeController controller;
  private final ModularTreeItem<?> parent;
  private final Consumer<? super T> setter;
  private final BorderPane container;

  private final Map<TypedReference<?>, ModularTreeItem<?>> childTreeItems = new HashMap<>();
  private boolean childrenCalculated;
  private TreeContributionAggregator contributionsAggregator;

  public ModularTreeItem(
      IEclipseContext context,
      TypedReference<T> data,
      ModularTreeController controller) {
    this(context, data, null, controller, null);
  }

  public ModularTreeItem(
      IEclipseContext context,
      TreeEntryChild<T> data,
      ModularTreeItem<?> parent) {
    this(
        context,
        data.getTypedObject(),
        data.isMutable() ? data::setObject : null,
        parent.controller,
        parent);
  }

  private ModularTreeItem(
      IEclipseContext context,
      TypedReference<T> data,
      Consumer<? super T> setter,
      ModularTreeController controller,
      ModularTreeItem<?> parent) {
    this.context = context;
    this.setter = setter;
    this.controller = controller;
    this.parent = parent;
    this.type = data.getTypeToken();

    container = new BorderPane();
    setGraphic(container);

    setValue(new TreeEntryImpl(data.getObject()));

    refreshImpl(true);
  }

  /**
   * @return the {@link TreeEntryImpl tree item data} for this tree node
   */
  @SuppressWarnings("unchecked")
  public TreeEntryImpl getEntry() {
    return (TreeEntryImpl) getValue();
  }

  public T getData() {
    return getEntry().data();
  }

  private void updateValue() {
    setValue(new TreeEntryImpl(getData()));
  }

  private void updateValue(T data) {
    if (setter == null) {
      throw new UnsupportedOperationException();
    }
    setValue(new TreeEntryImpl(data));
    setter.accept(data);
  }

  protected ModularTreeItem<?> createChild(TreeEntryChild<?> data) {
    return new ModularTreeItem<>(context, data, this);
  }

  @Override
  public ObservableList<TreeItem<TreeEntry<?>>> getChildren() {
    if (!childrenCalculated) {
      refreshContributions();
      rebuildChildren();
    }

    return super.getChildren();
  }

  public Optional<ModularTreeItem<?>> getModularParent() {
    return Optional.ofNullable(parent);
  }

  public Stream<ModularTreeItem<?>> getModularChildren() {
    return getChildren().stream().map(c -> (ModularTreeItem<?>) c);
  }

  private void refreshImpl(boolean recursive) {
    boolean selected = false; // TODO
    boolean focused = false; // TODO

    refreshContributions();

    if (recursive) {
      rebuildChildren();
    }

    if (selected || focused) {
      // TODO reselect and focus
    }
  }

  private void rebuildChildren() {
    List<TreeItem<TreeEntry<?>>> childrenItems;

    List<TreeEntryChild<?>> children = contributionsAggregator.getChildren().collect(toList());

    if (children.isEmpty()) {
      if (isExpanded()) {
        childTreeItems
            .keySet()
            .retainAll(children.stream().map(TreeEntryChild::getTypedObject).collect(toList()));

        childrenItems = children.stream().map(i -> {
          TypedReference<?> typed = i.getTypedObject();
          ModularTreeItem<?> treeItem = childTreeItems.get(typed);

          if (treeItem == null) {
            treeItem = createChild(i);
            childTreeItems.put(typed, treeItem);
          } else {
            treeItem.refreshImpl(true);
          }

          return treeItem;
        }).collect(toList());

        childrenCalculated = true;
      } else {
        childrenItems = Arrays.asList(new TreeItem<>());
        childrenCalculated = false;
      }
    } else {
      setExpanded(false);
      childTreeItems.clear();

      childrenItems = Collections.emptyList();
      childrenCalculated = true;
    }

    super.getChildren().setAll(childrenItems);
  }

  void refreshContributions() {
    refreshContributions(false);
  }

  void refreshContributions(boolean editing) {
    HBox node = new HBox();
    container.setCenter(node);

    contributionsAggregator = new TreeContributionAggregator(context, getEntry(), node, editing);

    controller.getContributors().forEach(contributionsAggregator::inject);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> U getAdapter(Class<U> adapter) {
    if (adapter == TreeEntry.class) {
      return (U) getEntry();
    }

    return getEntry().getAdapter(adapter);
  }

  boolean isEditable() {
    return contributionsAggregator.isEditable();
  }

  public void delete() {
    TreeDragCandidateImpl<?> dragCandidate = getDragCandidate();
    if (dragCandidate.getTransferModes().contains(DISCARD)) {
      dragCandidate.handleDrag(DISCARD);
      parent.getEntry().refresh(true);
    }
  }

  TreeDragCandidateImpl<?> getDragCandidate() {
    return getModularParent()
        .map(p -> p.contributionsAggregator.getDragAndDrop().getDragCandidate(getEntry()))
        .orElseGet(() -> new TreeDragCandidateImpl<>(emptyList(), getEntry()));
  }

  TreeDropCandidates getDropCandidates(Clipboard clipboard, TreeDropPosition position) {
    if (position == TreeDropPosition.OVER) {
      return contributionsAggregator.getDragAndDrop().getDropCandidates(clipboard, position, null);
    } else {
      return getModularParent()
          .map(
              p -> p.contributionsAggregator
                  .getDragAndDrop()
                  .getDropCandidates(clipboard, position, getEntry()))
          .orElseGet(() -> new TreeDropCandidates(emptyList(), clipboard, position, getEntry()));
    }
  }

  public class TreeEntryImpl implements TreeEntry<T>, IAdaptable {
    private final T data;

    public TreeEntryImpl(T data) {
      this.data = data;
    }

    @Override
    public String toString() {
      return data.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> U getAdapter(Class<U> adapter) {
      if (adapter == TreeEntry.class) {
        return (U) this;
      }

      if (adapter == type().getErasedType()) {
        return (U) data();
      }

      return context.get(Adapter.class).adapt(data(), adapter);
    }

    @Override
    public TypedReference<T> typedData() {
      return TypedReference.typedObject(type, data);
    }

    @Override
    public void update() {
      updateValue();
    }

    @Override
    public void update(T data) {
      updateValue(data);
    }

    @Override
    public Optional<TreeEntry<?>> parent() {
      return getModularParent().map(ModularTreeItem::getEntry);
    }

    @Override
    public void refresh(boolean recursive) {
      Platform.runLater(() -> {
        refreshImpl(recursive);
      });
    }

    @Override
    public Stream<TreeEntry<?>> children() {
      return getModularChildren().map(c -> c.getEntry());
    }
  }
}
