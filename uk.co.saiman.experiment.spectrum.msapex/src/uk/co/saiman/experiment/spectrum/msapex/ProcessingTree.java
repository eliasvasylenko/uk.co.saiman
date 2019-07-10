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
 * This file is part of uk.co.saiman.experiment.spectrum.msapex.
 *
 * uk.co.saiman.experiment.spectrum.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.spectrum.msapex;

import static java.util.stream.Collectors.toList;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.msapex.ProcessorCell;

public class ProcessingTree {
  public static final String ID = "uk.co.saiman.experiment.processing.tree";

  @Inject
  public void prepare(IEclipseContext context, Processing processing, ChildrenService children) {
    children
        .setItems(
            ProcessorCell.ID,
            DataProcessor.class,
            processing.steps().collect(toList()),
            r -> context.modify(Processing.class, new Processing(r)));
  }
}
