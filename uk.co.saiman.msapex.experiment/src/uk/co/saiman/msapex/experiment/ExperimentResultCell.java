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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import static uk.co.saiman.experiment.ExperimentLifecycleState.COMPLETE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.experiment.ExperimentStep;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentResultCell {
  public static final String ID = "uk.co.saiman.msapex.experiment.cell.node";

  @Inject
  private ExperimentStep<?> experiment;

  @PostConstruct
  public void prepare(Cell cell) {
    /*
     * configure label
     */
    if (experiment.getLifecycleState() == COMPLETE) {
      cell
          .setIconURI(
              "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/document-binary.png");
    } else {
      cell
          .setIconURI(
              "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/document.png");
    }
  }
}
