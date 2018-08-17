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
 * This file is part of uk.co.saiman.msapex.experiment.sample.
 *
 * uk.co.saiman.msapex.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.sample.treecontributions;

import static uk.co.saiman.eclipse.ui.ListItems.ITEM_DATA;

import org.eclipse.e4.ui.di.AboutToShow;

import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.experiment.sample.SampleConfiguration;

public class SampleExperimentNodeCell {
  public static final String ID = "uk.co.saiman.experiment.sample.cell";

  @AboutToShow
  public void prepare(Cell cell, @AdaptNamed(ITEM_DATA) SampleConfiguration data) {
    cell.setLabel(data.getName());
    // TODO setSupplemental(data.toString());
  }
}
