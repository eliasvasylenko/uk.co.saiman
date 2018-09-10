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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
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

  @Inject
  public ChildrenServiceImpl() {}

  private MElementContainer<Cell> parent() {
    return parentCell != null ? parentCell : parentTree;
  }

  private Cell readyModel(String id) {
    MUIElement model = (Cell) models.cloneSnippet(application, id, null);
    if (model == null || !(model instanceof Cell)) {
      throw new IllegalArgumentException("Child does not exist " + id);
    }
    Cell cell = (Cell) model;
    return cell;
  }

  @Override
  public <T> void setItem(String id, String contextName, T child) {
    Cell model = readyModel(id);
    model.getPersistedState().put(UIAddon.CHILD_CONTEXT_VALUE, contextName);
    model.getTransientData().put(UIAddon.CHILD_CONTEXT_VALUE, child);
    parent().getChildren().add(model);
  }

  @Override
  public <T> void setItem(String id, String contextName, T child, Consumer<? super T> update) {
    Cell model = readyModel(id);
    model.getPersistedState().put(UIAddon.CHILD_CONTEXT_VALUE, contextName);
    model.getTransientData().put(UIAddon.CHILD_CONTEXT_VALUE, child);
    model.getTransientData().put(UIAddon.CHILD_CONTEXT_VALUE_SET, (Consumer<? super T>) value -> {
      update.accept(value);
      invalidate();
    });
    parent().getChildren().add(model);
  }

  @Override
  public <T> void setItems(String id, String contextName, Collection<? extends T> children) {
    for (Object child : children) {
      Cell model = readyModel(id);
      model.getPersistedState().put(UIAddon.CHILD_CONTEXT_VALUE, contextName);
      model.getTransientData().put(UIAddon.CHILD_CONTEXT_VALUE, child);
      parent().getChildren().add(model);
    }
  }

  @Override
  public <T> void setItems(
      String id,
      String contextName,
      List<? extends T> children,
      Consumer<? super List<? extends T>> update) {
    for (Object child : children) {
      Cell model = readyModel(id);
      model.getPersistedState().put(UIAddon.CHILD_CONTEXT_VALUE, contextName);
      model.getTransientData().put(UIAddon.CHILD_CONTEXT_VALUE, child);
      model
          .getTransientData()
          .put(UIAddon.CHILD_CONTEXT_VALUE_SET, (Consumer<? super List<? extends T>>) value -> {
            update.accept(value);
            invalidate();
          });
      parent().getChildren().add(model);
    }
  }

  @Override
  public void invalidate() {
    for (Cell child : parent().getChildren()) {
      if (child.getPersistedState().containsKey(UIAddon.CHILD_CONTEXT_VALUE)) {
        child.setParent(null);
      }
    }
    context
        .set(ChildrenService.class, ContextInjectionFactory.make(ChildrenService.class, context));
  }
}