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

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.ui.SaiUiModel.PRIMARY_CONTEXT_KEY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Tree;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.eclipse.ui.SaiUiModel;

public class ChildrenServiceImpl implements ChildrenService {
  private class Children {
    private class Child {
      private final Cell cell;
      private Object item;

      public Child(Object item) {
        this.cell = (Cell) models.cloneSnippet(application, modelElementId, null);
        if (this.cell == null) {
          throw new IllegalArgumentException("Child does not exist " + modelElementId);
        }
        this.cell.getProperties().put(PRIMARY_CONTEXT_KEY, contextName);
        this.cell.getTags().add(SaiUiModel.NO_AUTO_HIDE);
        this.item = item;
      }

      public Cell cell() {
        return cell;
      }

      public Object item() {
        return item;
      }

      public Child setItem(Object item) {
        this.item = item;
        return this;
      }
    }

    private final String modelElementId;

    private String contextName;

    private List<Child> children = new ArrayList<>();

    private Consumer<? super List<?>> updatePlural;
    private Consumer<Object> updateSingle;

    public Children(String modelElementId) {
      this.modelElementId = modelElementId;
    }

    public boolean isModifiable() {
      return updateSingle != null || updatePlural != null;
    }

    public java.util.Optional<Child> getChild(Cell cell) {
      return children.stream().filter(c -> c.cell() == cell).findAny();
    }

    public Object getValue(Cell cell) {
      return getChild(cell).map(Child::item).orElse(null);
    }

    public void addChildren() {
      /*
       * 
       * 
       * TODO only remove and re-add the items we need to to get the correct order.
       * 
       * 
       */
      for (Iterator<Cell> i = parent().getChildren().iterator(); i.hasNext();) {
        Cell cell = i.next();
        if (cell.getElementId().equals(modelElementId)) {
          i.remove();
        }
      }
      parent().getChildren().addAll(children.stream().map(Child::cell).collect(toList()));
    }

    public void updateChildren() {
      if (updateSingle != null) {
        updateSingle.accept(children.get(0).item());
      } else if (updatePlural != null) {
        updatePlural
            .accept(children.stream().map(Child::item).filter(Objects::nonNull).collect(toList()));
      }
    }

    public boolean updateChild(Cell cell, Object value) {
      if (!parent().getChildren().contains(cell)) {
        cell.setToBeRendered(false);
        return false;
      }
      Child child = getChild(cell).orElse(null);
      if (child == null) {
        cell.setToBeRendered(false);
        return false;
      }
      child.setItem(value);
      return true;
    }

    public synchronized void setItem(String contextName, Object item, Consumer<Object> update) {
      if (Objects.equals(this.contextName, contextName)
          && !(update == null && isModifiable())
          && children.size() == 1) {
        children.get(0).setItem(item);
      } else {
        this.contextName = contextName;
        children = singletonList(new Child(item));
      }
      updateSingle = update;
      updatePlural = null;
      addChildren();
    }

    public synchronized void setItems(
        String contextName,
        Collection<?> items,
        Consumer<? super Collection<?>> update) {
      if (Objects.equals(this.contextName, contextName) && !(update == null && isModifiable())) {
        Map<Object, Child> previousChildren = new LinkedHashMap<>();
        children.stream().forEach(child -> previousChildren.putIfAbsent(child.item(), child));
        children = items.stream().map(item -> {
          Child child = previousChildren.remove(item);
          if (child != null) {
            child.setItem(item);
            return child;
          } else {
            return new Child(item);
          }
        }).collect(toList());
      } else {
        this.contextName = contextName;
        children.stream().map(Child::cell).forEach(parent().getChildren()::remove);
        children = items.stream().map(Child::new).collect(toList());
      }
      updateSingle = null;
      updatePlural = update;
      addChildren();
    }
  }

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

  private final Map<String, Children> children = new HashMap<>();

  @Inject
  public ChildrenServiceImpl() {}

  private MElementContainer<Cell> parent() {
    return parentCell != null ? parentCell : parentTree;
  }

  private Children getChildren(String modelElementId) {
    return children.computeIfAbsent(modelElementId, k -> new Children(modelElementId));
  }

  @Override
  public void setItem(String modelElementId, String contextName, Object child) {
    getChildren(modelElementId).setItem(contextName, child, null);
  }

  @Override
  public void setItem(
      String modelElementId,
      String contextName,
      Object child,
      Consumer<Object> update) {
    getChildren(modelElementId).setItem(contextName, child, update);
  }

  @Override
  public void setItems(String modelElementId, String contextName, Collection<?> children) {
    getChildren(modelElementId).setItems(contextName, children, null);
  }

  @Override
  public void setItems(
      String modelElementId,
      String contextName,
      Collection<?> children,
      Consumer<? super Collection<?>> update) {
    getChildren(modelElementId).setItems(contextName, children, update);
  }

  static void prepareChildContainer(IEclipseContext context) {
    try {
      context
          .set(
              ChildrenService.class,
              ContextInjectionFactory.make(ChildrenServiceImpl.class, context));
      context.declareModifiable(ChildrenService.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void prepareChild(IEclipseContext context, Cell cell) {
    IEclipseContext parentContext = ((MContext) cell.getParent()).getContext();
    ChildrenServiceImpl parentService = (ChildrenServiceImpl) parentContext
        .get(ChildrenService.class);

    parentService.prepareChildImpl(context, cell);
  }

  private void prepareChildImpl(IEclipseContext context, Cell cell) {
    String key = cell.getProperties().get(PRIMARY_CONTEXT_KEY);
    if (key == null)
      return;

    Children children = getChildren(cell.getElementId());
    Object value = children.getValue(cell);
    if (value == null)
      return;

    context.set(key, value);

    if (!children.isModifiable())
      return;

    context.declareModifiable(key);

    RunAndTrack runAndTrack = new RunAndTrack() {
      private boolean firstTry = true;

      @Override
      public boolean changed(IEclipseContext c) {
        Object value = c.get(key);
        if (firstTry) {
          firstTry = false;
          return true;
        }
        boolean updated = children.updateChild(cell, value);
        runExternalCode(children::updateChildren);
        return updated;
      }
    };
    context.runAndTrack(runAndTrack);
  }
}
