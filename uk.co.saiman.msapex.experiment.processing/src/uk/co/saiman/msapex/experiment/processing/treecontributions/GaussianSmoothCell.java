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
package uk.co.saiman.msapex.experiment.processing.treecontributions;

import org.eclipse.e4.ui.di.AboutToShow;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.processing.GaussianSmooth;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.property.Property;

public class GaussianSmoothCell {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.gaussiansmooth";

  @AboutToShow
  public void prepare(HBox node, Property<GaussianSmooth> entry, ChildrenService children) {
    // TODO setSupplemental(node,
    // Double.toString(entry.get().getStandardDeviation()));

    children
        .setItem(
            StandardDeviation.ID,
            double.class,
            entry.get().getStandardDeviation(),
            result -> entry.set(entry.get().withStandardDeviation(result)));
  }

  public static class StandardDeviation {
    public static final String ID = GaussianSmoothCell.ID + ".deviation";

    @AboutToShow
    void prepare(Cell cell, double standardDeviation, @Localize ProcessingProperties properties) {
      cell.setLabel(properties.standardDeviationLabel().get());
    }
  }
}
