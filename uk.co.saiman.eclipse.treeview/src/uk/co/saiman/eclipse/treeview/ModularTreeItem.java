/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static javafx.css.PseudoClass.getPseudoClass;
import static org.eclipse.e4.core.internal.contexts.ContextObjectSupplier.getObjectSupplier;
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
import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.InjectorFactory;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;
import org.eclipse.e4.ui.di.AboutToShow;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.TreeClipboardManager.TreeDragCandidateImpl;
import uk.co.saiman.eclipse.treeview.TreeClipboardManager.TreeDropCandidates;
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
  final private static IInjector INJECTOR = InjectorFactory.getDefault();

  private final ModularTreeController controller;
  private final ModularTreeItem<?> parent;
  private final TreeEntryImpl entry;
  private final TypedReference<T> data;
  private final BorderPane container;

  private final Map<TypedReference<?>, ModularTreeItem<?>> childTreeItems = new HashMap<>();
  private boolean childrenCalculated;
  private TreeChildrenImpl children;
  private TreeEditorImpl<T> editor;
  private TreeClipboardManager dragAndDrop;
  private HBox node;

  protected ModularTreeItem(TypedReference<T> data, ModularTreeController controller) {
    this(data, controller, null);
  }

  protected ModularTreeItem(TypedReference<T> data, ModularTreeItem<?> parent) {
    this(data, parent.controller, parent);
  }

  private ModularTreeItem(
      TypedReference<T> data,
      ModularTreeController controller,
      ModularTreeItem<?> parent) {
    this.data = data;
    this.controller = controller;
    this.parent = parent;
    this.entry = new TreeEntryImpl();

    container = new BorderPane();
    container.pseudoClassStateChanged(getPseudoClass("fuck"), true);
    setGraphic(container);

    setValue(entry);
    refreshImpl(true);
  }

  protected ModularTreeItem<?> createChild(TypedReference<?> data) {
    return new ModularTreeItem<>(data, this);
  }

  /**
   * @return the {@link TreeEntryImpl tree item data} for this tree node
   */
  public TreeEntryImpl getEntry() {
    return entry;
  }

  @Override
  public ObservableList<TreeItem<TreeEntry<?>>> getChildren() {
    if (!childrenCalculated) {
      refreshContributions(null);
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

    refreshContributions(null);

    if (recursive) {
      rebuildChildren();
    }

    if (selected || focused) {
      // TODO reselect and focus
    }
  }

  private void rebuildChildren() {
    List<TreeItem<TreeEntry<?>>> childrenItems;

    if (children.hasChildren()) {
      if (isExpanded()) {
        childTreeItems.keySet().retainAll(children.getChildren().collect(toList()));

        childrenItems = children.getChildren().map(i -> {
          ModularTreeItem<?> treeItem = childTreeItems.get(i);

          if (treeItem == null) {
            treeItem = createChild(i);
            childTreeItems.put(i, treeItem);
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

  void refreshContributions(Runnable commitEdit) {
    node = new HBox();
    children = new TreeChildrenImpl();
    editor = new TreeEditorImpl<>(commitEdit);
    dragAndDrop = new TreeClipboardManager();

    container.setCenter(node);

    PrimaryObjectSupplier contextSupplier = getObjectSupplier(controller.getContext(), INJECTOR);
    PrimaryObjectSupplier localSupplier = new TreeContributionObjectSupplier(
        entry,
        node,
        children,
        editor,
        dragAndDrop);

    Consumer<Object> injector = c -> INJECTOR
        .invoke(c, AboutToShow.class, null, contextSupplier, localSupplier);

    controller.getContributors().forEach(injector);
    injector.accept(data.getObject());
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
    return editor.isEditable();
  }

  void delete() {
    TreeDragCandidateImpl<?> dragCandidate = getDragCandidate();
    if (dragCandidate.getTransferModes().contains(DISCARD)) {
      dragCandidate.handleDrag(DISCARD);
      parent.getEntry().refresh(true);
    }
  }

  TreeDragCandidateImpl<?> getDragCandidate() {
    return getModularParent()
        .map(p -> p.dragAndDrop.getDragCandidate(entry))
        .orElseGet(() -> new TreeDragCandidateImpl<>(emptyList(), entry));
  }

  TreeDropCandidates getDropCandidates(Clipboard clipboard, TreeDropPosition position) {
    if (position == TreeDropPosition.OVER) {
      return dragAndDrop.getDropCandidates(clipboard, position, null);
    } else {
      return getModularParent()
          .map(p -> p.dragAndDrop.getDropCandidates(clipboard, position, getEntry()))
          .orElseGet(() -> new TreeDropCandidates(emptyList(), clipboard, position, getEntry()));
    }
  }

  public class TreeEntryImpl implements TreeEntry<T>, IAdaptable {
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

      return controller.adapt(this, adapter);
    }

    @Override
    public TypedReference<T> typedData() {
      return data;
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
