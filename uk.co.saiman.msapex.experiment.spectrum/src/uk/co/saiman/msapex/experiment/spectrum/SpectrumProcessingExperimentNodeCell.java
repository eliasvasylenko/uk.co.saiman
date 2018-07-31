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

import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;
import static uk.co.saiman.eclipse.ui.fx.TableService.setLabel;
import static uk.co.saiman.eclipse.ui.fx.TableService.setSupplemental;

import javax.inject.Named;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.spectrum.SpectrumResultConfiguration;
import uk.co.saiman.msapex.experiment.processing.treecontributions.ProcessorListCell;
import uk.co.saiman.msapex.experiment.treecontributions.ExperimentNodeCell;

@ServiceRanking(10)
@Component(name = SpectrumProcessingExperimentNodeCell.ID, service = MCell.class)
public class SpectrumProcessingExperimentNodeCell extends MCellImpl {
  public static final String ID = "uk.co.saiman.experiment.spectrum.cell.processing";
  public static final String PROCESSORS_ID = ID + ".processor";

  public SpectrumProcessingExperimentNodeCell() {
    super(ID, Contribution.class);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + ExperimentNodeCell.ID + ")")
  @Override
  public void setSpecialized(MCell specialized) {
    super.setSpecialized(specialized);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + ProcessorListCell.ID + ")")
  public void setChild(MCell processors) {
    MCellImpl child = new MCellImpl(PROCESSORS_ID, null);
    child.setSpecialized(processors);
    child.setParent(this);
  }

  public class Contribution {
    @AboutToShow
    public void prepare(
        HBox node,
        @Named(ENTRY_DATA) ExperimentNode<?, ?> data,
        @AdaptNamed(ENTRY_DATA) SpectrumResultConfiguration state,
        ListItems children) {
      setLabel(node, data.getType().getName());
      setSupplemental(node, state.getSpectrumName());

      children.getConfiguration(PROCESSORS_ID).setObject(state.getProcessing().collect(toList()));
    }
  }
}
