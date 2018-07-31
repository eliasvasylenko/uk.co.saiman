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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.osgi.service.component.annotations.Deactivate;

/**
 * A basic implementation of a cell specialization. Ultimately, according to the
 * intended documented plan for {@link MCell}, this will not be needed as the E4
 * model service will provide implementations.
 * 
 * @author Elias N Vasylenko
 */
public class MCellImpl implements MCell {
  private final String id;
  private final Class<?> contributionClass;

  private boolean editable;

  private MCellImpl specialized;
  private LinkedHashSet<MCellImpl> specializations;

  private MCellImpl parent;
  private List<MCellImpl> children;

  public MCellImpl(String id, Class<?> contributionClass) {
    this.id = id;
    this.contributionClass = contributionClass;
    this.children = new ArrayList<>();
  }

  @Deactivate
  void destroy() {
    setParent(null);
  }

  @Override
  public String getElementId() {
    return id;
  }

  @Override
  public MCell getSpecialized() {
    return specialized;
  }

  @Override
  public void setSpecialized(MCell specialized) {
    MCellImpl newSpecialized = (MCellImpl) specialized;

    if (this.specialized != null) {
      this.specialized.removeSpecialization(this);
    }
    if (newSpecialized != null) {
      newSpecialized.addSpecialization(this);
    }

    this.specialized = newSpecialized;
  }

  private synchronized void addSpecialization(MCellImpl mCellImpl) {
    specializations.add(this);
  }

  private synchronized void removeSpecialization(MCellImpl mCellImpl) {
    specializations.remove(this);
  }

  @Override
  public synchronized List<MCell> getSpecializations() {
    return new ArrayList<>(specializations);
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
  public Class<?> getContributionClass() {
    return contributionClass;
  }

  @Override
  public List<MCell> getChildren() {
    return new ArrayList<>(children);
  }

  @Override
  public MCell getParent() {
    return parent;
  }

  @Override
  public void setParent(MCell parent) {
    MCellImpl newParent = (MCellImpl) parent;

    if (this.parent != null) {
      this.parent.removeChild(this);
    }
    if (newParent != null) {
      newParent.addChild(this);
    }

    this.parent = newParent;
  }

  private void addChild(MCell child) {
    children.add((MCellImpl) child);
  }

  private void removeChild(MCell child) {
    children.remove(child);
  }
}
