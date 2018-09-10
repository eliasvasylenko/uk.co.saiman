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
 * This file is part of uk.co.saiman.msapex.experiment.spectrum.
 *
 * uk.co.saiman.msapex.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.spectrum;

import org.eclipse.e4.ui.di.AboutToShow;

import uk.co.saiman.eclipse.adapter.AdaptClass;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;

public class SpectrumExperimentNodeCell {
  public static final String ID = "uk.co.saiman.experiment.spectrum.cell";

  @AboutToShow
  public void prepare(
      Cell cell,
      ExperimentNode<?, ?> data,
      @AdaptClass(ExperimentNode.class) SpectrumConfiguration state,
      ChildrenService children) {
    cell.setLabel(data.getType().getName());
    // TODO cell.setSupplemental(state.getSpectrumName());
  }
}
