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

/**
 * A basic implementation of a cell specialization. Ultimately, according to the
 * intended documented plan for {@link MCell}, this will not be needed as the E4
 * model service will provide implementations.
 * 
 * @author Elias N Vasylenko
 */
public class MTableImpl implements MTable {
  private final String id;
  private final Class<?> contributionClass;
  private MCell root;

  private boolean editable;

  public MTableImpl(String id, Class<?> contributionClass) {
    this.id = id;
    this.contributionClass = contributionClass;
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
  public Class<?> getContributionClass() {
    return contributionClass;
  }

  @Override
  public MCell getRootCell() {
    return root;
  }

  @Override
  public void setRootCell(MCell root) {
    this.root = root;
  }
}
