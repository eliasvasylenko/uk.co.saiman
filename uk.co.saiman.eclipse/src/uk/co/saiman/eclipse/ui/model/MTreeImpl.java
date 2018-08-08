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
package uk.co.saiman.eclipse.ui.model;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

/**
 * A basic implementation of a cell specialization. Ultimately, according to the
 * intended documented plan for {@link MCell}, this will not be needed as the E4
 * model service will provide implementations.
 * 
 * @author Elias N Vasylenko
 */
public class MTreeImpl implements MTree {
  private final String id;
  private final Class<?> contributionClass;
  private MCellImpl root;

  private boolean editable;

  private IEclipseContext context;
  private Object contributionObject;

  public MTreeImpl(String id, Class<?> contributionClass) {
    this.id = id;
    this.contributionClass = contributionClass;
  }

  public void initialize(IEclipseContext context) {
    context = context.createChild(id);
    context.set(MTree.class, this);
    if (this.context == null) {
      this.context = context;
      this.contributionObject = ContextInjectionFactory.make(contributionClass, context);
      root.initialize(context);
    }
  }

  @Override
  public Class<?> getContributionClass() {
    return contributionClass;
  }

  @Override
  public IEclipseContext getContext() {
    return context;
  }

  @Override
  public Object getObject() {
    return contributionObject;
  }

  @Override
  public String getElementId() {
    return id;
  }

  @Override
  public boolean isEditable() {
    return editable;
  }

  @Override
  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  @Override
  public MCell getRootCell() {
    return root;
  }

  @Override
  public void setRootCell(MCell root) {
    MCellImpl newRoot = (MCellImpl) root;
    newRoot.setParent(null);
    this.root = newRoot;
  }
}
