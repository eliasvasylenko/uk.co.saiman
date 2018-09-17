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

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.fx.ui.workbench.base.Util;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.CellContribution;

public class CellContributionHandler {
  @Inject
  public CellContributionHandler() {}

  public void handleContributionRemove(Cell parent, Collection<CellContribution> contributions) {
    // TODO Auto-generated method stub

  }

  public void handleContributionAddition(Cell parent, Collection<CellContribution> contributions) {
    IContributionFactory contributionFactory = parent.getContext().get(IContributionFactory.class);

    for (CellContribution contribution : contributions) {
      createContext(contribution, parent.getContext());

      Object newContribution = contributionFactory
          .create(contribution.getContributionURI(), contribution.getContext());
      contribution.setObject(newContribution);
    }
  }

  private static IEclipseContext createContext(
      CellContribution model,
      IEclipseContext parentContext) {
    IEclipseContext lclContext = parentContext.createChild(getContextName(model));
    Util.setup(model, lclContext);
    return lclContext;
  }

  private static String getContextName(CellContribution element) {
    StringBuilder builder = new StringBuilder(element.getClass().getSimpleName());
    String elementId = element.getElementId();
    if (elementId != null && elementId.length() != 0) {
      builder.append(" (").append(elementId).append(") ");
    }
    builder.append("Context");
    return builder.toString();
  }

}
