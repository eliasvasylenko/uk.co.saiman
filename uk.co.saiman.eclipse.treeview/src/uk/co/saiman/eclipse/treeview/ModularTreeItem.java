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

import static java.util.stream.Collectors.toList;
import static org.eclipse.e4.core.internal.contexts.ContextObjectSupplier.getObjectSupplier;
import static uk.co.saiman.reflection.Types.getErasedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;
import org.eclipse.e4.ui.di.AboutToShow;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
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
  private final ModularTreeController controller;
  private final ModularTreeItem<?> parent;
  private final TreeEntryImpl entry;
  private final TypedReference<T> data;

  private final Map<TypedReference<?>, ModularTreeItem<?>> childTreeItems = new HashMap<>();
  private boolean childrenCalculated;
  private List<TypedReference<?>> children;
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
      refreshContributions();
      rebuildChildren();
    }

    return super.getChildren();
  }

  private void refreshImpl(boolean recursive) {
    boolean selected = false; // TODO
    boolean focused = false; // TODO

    refreshContributions();

    setValue(null);
    setValue(entry);

    if (recursive) {
      rebuildChildren();
    }

    if (selected || focused) {
      // TODO reselect and focus
    }
  }

  protected void rebuildChildren() {
    boolean hasChildren = !children.isEmpty();

    List<TreeItem<TreeEntry<?>>> childrenItems;

    if (hasChildren) {
      if (isExpanded()) {
        childTreeItems.keySet().retainAll(children);

        childrenItems = children.stream().map(i -> {
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

  private void refreshContributions() {
    IEclipseContext context = controller.getContext();

    children = new ArrayList<>();
    node = new HBox();

    controller.getContributors().forEach(c -> {
      PrimaryObjectSupplier contextSupplier = getObjectSupplier(context, controller.getInjector());
      PrimaryObjectSupplier localSupplier = new PrimaryObjectSupplier() {
        @Override
        public void resumeRecording() {}

        @Override
        public void pauseRecording() {}

        @Override
        public void get(
            IObjectDescriptor[] descriptors,
            Object[] actualValues,
            IRequestor requestor,
            boolean initial,
            boolean track,
            boolean group) {
          for (int i = 0; i < descriptors.length; i++) {
            Type desired = descriptors[i].getDesiredType();

            if (desired.equals(new TypeToken<List<TypedReference<?>>>() {}.getType())) {
              actualValues[i] = children;

            } else if (desired == HBox.class) {
              actualValues[i] = node;

            } else if (desired instanceof ParameterizedType
                && getErasedType(desired) == TreeEntry.class
                && TypeToken
                    .forType(desired)
                    .getTypeArguments()
                    .findFirst()
                    .get()
                    .getTypeToken()
                    .isAssignableFrom(getEntry().type())) {
              actualValues[i] = getEntry();
            }
          }
        }
      };
      controller.getInjector().invoke(c, AboutToShow.class, null, contextSupplier, localSupplier);
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> U getAdapter(Class<U> adapter) {
    if (adapter == TreeEntry.class) {
      return (U) getEntry();
    }

    return getEntry().getAdapter(adapter);
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

    protected HBox getNode() {
      return node;
    }

    @Override
    public TypedReference<T> typedData() {
      return data;
    }

    @Override
    public Optional<TreeEntry<?>> parent() {
      return Optional.ofNullable(parent).map(ModularTreeItem::getEntry);
    }

    @Override
    public void refresh(boolean recursive) {
      Platform.runLater(() -> {
        refreshImpl(recursive);
      });
    }
  }
}
