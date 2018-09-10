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
import uk.co.saiman.experiment.processing.Convolution;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.property.Property;

public class ConvolutionCell {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.convolution";

  @AboutToShow
  public void prepare(HBox node, Property<Convolution> entry, ChildrenService children) {
    // TODO setSupplemental(node,
    // Arrays.toString(entry.get().getConvolutionVector()));

    children
        .setItem(
            Vector.ID,
            double[].class,
            entry.get().getConvolutionVector(),
            result -> entry.set(entry.get().withConvolutionVector(result)));

    children
        .setItem(
            Centre.ID,
            int.class,
            entry.get().getConvolutionVectorCentre(),
            result -> entry.set(entry.get().withConvolutionVectorCentre(result)));
  }

  public static class Vector {
    public static final String ID = ConvolutionCell.ID + ".convolution";

    @AboutToShow
    public void prepare(
        Cell cell,
        double[] convolutionVector,
        @Localize ProcessingProperties properties) {
      cell.setLabel(properties.vectorLabel().get());
    }
  }

  public static class Centre {
    public static final String ID = ConvolutionCell.ID + ".centre";

    @AboutToShow
    public void prepare(
        Cell cell,
        int convolutionVectorCentre,
        @Localize ProcessingProperties properties) {
      cell.setLabel(properties.centreLabel().get());
    }
  }
}
