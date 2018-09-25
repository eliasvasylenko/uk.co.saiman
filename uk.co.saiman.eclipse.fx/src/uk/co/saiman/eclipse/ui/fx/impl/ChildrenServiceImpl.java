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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Tree;
import uk.co.saiman.eclipse.ui.ChildrenService;

public class ChildrenServiceImpl implements ChildrenService {
  @Inject
  private IEclipseContext context;
  @Inject
  private EModelService models;
  @Inject
  private MApplication application;

  @Inject
  @Optional
  private Cell parentCell;
  @Inject
  @Optional
  private Tree parentTree;

  private final Map<String, List<Cell>> children = new HashMap<>();

  @Inject
  public ChildrenServiceImpl() {}

  private MElementContainer<Cell> parent() {
    return parentCell != null ? parentCell : parentTree;
  }

  @PostConstruct
  private void findChildren() {
    for (Cell child : parent().getChildren()) {
      String contextValue = child.getContextValue();
      if (contextValue != null) {
        List<Cell> values = getChildren(child.getElementId());
        values.add(child);
      }
    }
  }

  private List<Cell> getChildren(String id) {
    return children.computeIfAbsent(id, k -> new ArrayList<>());
  }

  private Cell readyModel(String id, String contextName, Object value) {
    Cell cell = null;

    Iterator<Cell> cells = getChildren(id).iterator();
    while (cells.hasNext()) {
      Cell child = cells.next();

      if (Objects.equals(child.getTransientData().get(UIAddon.CHILD_CONTEXT_VALUE), value)) {
        cells.remove();
        cell = child;
        break;
      }
    }

    if (cell == null) {
      cell = (Cell) models.cloneSnippet(application, id, null);
      if (cell == null) {
        throw new IllegalArgumentException("Child does not exist " + id);
      }
      cell.setContextValue(contextName);
      cell.getTransientData().put(UIAddon.CHILD_CONTEXT_VALUE, value);
    }

    return cell;
  }

  private List<Cell> readyModels(
      String id,
      String contextName,
      Collection<? extends Object> values) {
    return values.stream().map(child -> readyModel(id, contextName, child)).collect(toList());
  }

  private void setChild(String id, Cell model) {
    setChildren(id, Collections.singletonList(model));
  }

  private void setChildren(String id, Collection<Cell> newChildren) {
    List<Cell> children = getChildren(id);
    children.forEach(c -> c.setParent(null));
    children.clear();

    parent().getChildren().addAll(newChildren);
    children.addAll(newChildren);
  }

  @Override
  public <T> void setItem(String id, String contextName, T child) {
    Cell model = readyModel(id, contextName, child);
    setChild(id, model);
  }

  @Override
  public <T> void setItem(String id, String contextName, T child, Consumer<? super T> update) {
    Cell model = readyModel(id, contextName, child);
    model.getTransientData().put(UIAddon.CHILD_CONTEXT_VALUE_SET, (Consumer<? super T>) value -> {
      update.accept(value);
      invalidate();
    });
    setChild(id, model);
  }

  @Override
  public <T> void setItems(String id, String contextName, Collection<? extends T> children) {
    List<Cell> models = readyModels(id, contextName, children);
    setChildren(id, models);
  }

  @Override
  public <T> void setItems(
      String id,
      String contextName,
      List<? extends T> children,
      Consumer<? super List<? extends T>> update) {
    List<Cell> models = readyModels(id, contextName, children);
    int i = 0;
    for (Cell model : models) {
      int index = i++;

      Map<String, Object> data = model.getTransientData();
      data.put(UIAddon.CHILD_CONTEXT_VALUES_SET, (Consumer<? super List<? extends T>>) value -> {
        update.accept(value);
        invalidate();
      });
      data.put(UIAddon.CHILD_CONTEXT_VALUE_SET, (Consumer<? super T>) value -> {
        List<T> newChildren = new ArrayList<>(children);
        if (value == null) {
          newChildren.remove(index);
        } else {
          newChildren.set(index, value);
        }
        update.accept(newChildren);
        invalidate();
      });
    }
    setChildren(id, models);
  }

  @Override
  public void invalidate() {
    context
        .modify(
            ChildrenService.class,
            ContextInjectionFactory.make(ChildrenServiceImpl.class, context));
  }
}
