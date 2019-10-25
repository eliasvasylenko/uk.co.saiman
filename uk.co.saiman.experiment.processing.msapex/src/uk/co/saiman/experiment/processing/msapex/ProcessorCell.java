/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.processing.msapex.
 *
 * uk.co.saiman.experiment.processing.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing.msapex;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.utilities.EclipseContextUtilities;

public class ProcessorCell {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.processor";

  @Inject
  private IEclipseContext context;

  @Inject
  private MCell cell;

  @PostConstruct
  public void prepare() {
    EclipseContextUtilities.injectSubtypes(context, DataProcessor.class);
  }

  /*
   * 
   * 
   * 
   * 
   * TODO Should use a similar system to adding child steps. Each processor should
   * have its own menu item snippet and the context menu can scrape the model to
   * collect them all. That way if any special UI needs to be presented it will be
   * straightforward, with total flexibility. It also means localization is taken
   * care of.
   * 
   * Problem with this (same as with the experiment step adding system) there is
   * no good way to carry the icon selection forward to the cell renderer.
   * 
   * 
   * 
   * 
   * TODO the "eclipse e4 way" to solve this might be to add custom annotations to
   * execute on the "object" of the model item to identify which processors they
   * can apply to e.g.
   * 
   * @CanApply boolean appliesTo(DataProcessor p) { return p instanceof
   * GaussianProcessor; }
   * 
   * 
   * 
   * 
   * 
   */

  @Inject
  public void inject(DataProcessor entry) {
    if (entry != null) {
      cell.setLabel(entry.getClass().getName());
    }
  }
}
