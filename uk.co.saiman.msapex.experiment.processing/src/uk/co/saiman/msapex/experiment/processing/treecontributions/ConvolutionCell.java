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

import static org.osgi.framework.Constants.SERVICE_PID;
import static uk.co.saiman.eclipse.ui.fx.TableService.setLabel;
import static uk.co.saiman.eclipse.ui.fx.TableService.setSupplemental;

import java.util.Arrays;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.eclipse.variable.NamedVariable;
import uk.co.saiman.experiment.processing.Convolution;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.property.Property;

@Component(name = ConvolutionCell.ID, service = MCell.class)
public class ConvolutionCell extends MCellImpl {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.convolution";
  public static final String VECTOR_ID = ID + ".vector";
  public static final String CENTRE_ID = ID + ".centre";

  public ConvolutionCell() {
    super(ID, Contribution.class);

    new MCellImpl(VECTOR_ID, Vector.class).setParent(this);
    new MCellImpl(CENTRE_ID, Centre.class).setParent(this);
  }

  @Reference(target = "(" + SERVICE_PID + "=" + ProcessorCell.ID + ")")
  @Override
  public void setSpecialized(MCell specialized) {
    super.setSpecialized(specialized);
  }

  public class Contribution {
    @AboutToShow
    public void prepare(
        HBox node,
        @NamedVariable(ENTRY_DATA) Property<Convolution> entry,
        ListItems children) {
      setSupplemental(node, Arrays.toString(entry.get().getConvolutionVector()));

      children
          .<double[]>getConfiguration(VECTOR_ID)
          .setObject(entry.get().getConvolutionVector())
          .setUpdateFunction((i, result) -> entry.set(entry.get().withConvolutionVector(result)));

      children
          .<Integer>getConfiguration(CENTRE_ID)
          .setObject(entry.get().getConvolutionVectorCentre())
          .setUpdateFunction(
              (i, result) -> entry.set(entry.get().withConvolutionVectorCentre(result)));
    }
  }

  static class Vector {
    @AboutToShow
    public void prepare(HBox vectorNode, @Localize ProcessingProperties properties) {
      setLabel(vectorNode, properties.vectorLabel().get());
    }
  }

  static class Centre {
    @AboutToShow
    public void prepare(HBox vectorNode, @Localize ProcessingProperties properties) {
      setLabel(vectorNode, properties.centreLabel().get());
    }
  }
}
