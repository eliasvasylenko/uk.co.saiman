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
 * This file is part of uk.co.saiman.msapex.experiment.processing.
 *
 * uk.co.saiman.msapex.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.processing;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.utilities.EclipseUtilities;

public class ProcessorCell {
  public static final String ID = "uk.co.saiman.msapex.experiment.processing.cell.processor";

  @Inject
  private IEclipseContext context;

  @Inject
  private Cell cell;

  @PostConstruct
  public void prepare() {
    EclipseUtilities.injectSupertypes(context, DataProcessor.class);
  }

  @Inject
  public void inject(DataProcessor entry) {
    if (entry != null) {
      cell.setLabel(entry.getClass().getName());
    }
  }
}
