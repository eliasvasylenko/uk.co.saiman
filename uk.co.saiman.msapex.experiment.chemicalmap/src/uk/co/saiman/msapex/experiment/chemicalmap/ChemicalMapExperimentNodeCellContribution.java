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
 * This file is part of uk.co.saiman.msapex.experiment.chemicalmap.
 *
 * uk.co.saiman.msapex.experiment.chemicalmap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.chemicalmap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.chemicalmap;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.adapter.AdaptClass;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.chemicalmap.ChemicalMapConfiguration;

public class ChemicalMapExperimentNodeCellContribution {
  public static final String ID = "uk.co.saiman.experiment.treecontribution.chemicalmap";

  @PostConstruct
  public void prepare(
      HBox node,
      Cell cell,
      ExperimentNode<?, ?> experiment,
      @Optional @AdaptClass(ExperimentNode.class) ChemicalMapConfiguration configuration) {
    if (configuration != null) {
      cell.setLabel(experiment.getType().getName());
      // TODO cell.setSupplemental(node, configuration.getChemicalMapName());
    }
  }
}
