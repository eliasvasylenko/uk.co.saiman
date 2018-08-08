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

import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;
import static uk.co.saiman.eclipse.ui.fx.TreeService.setLabel;
import static uk.co.saiman.eclipse.ui.fx.TreeService.setSupplemental;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.eclipse.variable.NamedVariable;
import uk.co.saiman.experiment.processing.GaussianSmooth;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.property.Property;

@Component(name = GaussianSmoothCell.ID, service = MCell.class)
public class GaussianSmoothCell extends MCellImpl {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.gaussiansmooth";
  public static final String STANDARD_DEVIATION_ID = ID + ".deviation";

  public GaussianSmoothCell() {
    super(ID, Contribution.class);

    new MCellImpl(STANDARD_DEVIATION_ID, StandardDeviation.class).setParent(this);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + ProcessorCell.ID + ")")
  @Override
  public void setSpecialized(MCell specialized) {
    super.setSpecialized(specialized);
  }

  public class Contribution {
    @AboutToShow
    public void prepare(
        HBox node,
        @NamedVariable(ENTRY_DATA) Property<GaussianSmooth> entry,
        ListItems children) {
      setSupplemental(node, Double.toString(entry.get().getStandardDeviation()));

      children
          .addItem(
              STANDARD_DEVIATION_ID,
              entry.get().getStandardDeviation(),
              result -> entry.set(entry.get().withStandardDeviation(result)));
    }
  }

  public static class StandardDeviation {
    @AboutToShow
    void prepare(HBox deviationNode, @Localize ProcessingProperties properties) {
      setLabel(deviationNode, properties.standardDeviationLabel().get());
    }
  }
}
