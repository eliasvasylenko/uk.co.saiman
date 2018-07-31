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

import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;
import static uk.co.saiman.eclipse.ui.fx.TableService.setLabel;
import static uk.co.saiman.eclipse.ui.fx.TableService.setSupplemental;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.experiment.sample.SampleConfiguration;
import uk.co.saiman.msapex.experiment.treecontributions.ExperimentNodeCell;

@ServiceRanking(100)
@Component(name = SampleExperimentNodeCell.ID, service = MCell.class)
public class SampleExperimentNodeCell extends MCellImpl {
  public static final String ID = "uk.co.saiman.experiment.sample.cell";

  public SampleExperimentNodeCell() {
    super(ID, Contribution.class);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + ExperimentNodeCell.ID + ")")
  @Override
  public void setSpecialized(MCell specialized) {
    super.setSpecialized(specialized);
  }

  public class Contribution {
    @AboutToShow
    public void prepare(HBox node, @AdaptNamed(ENTRY_DATA) SampleConfiguration data) {
      setLabel(node, data.getName());
      setSupplemental(node, data.toString());
    }
  }
}
