/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.experiment.graph.ExperimentPath;
import uk.co.saiman.experiment.production.Observation;
import uk.co.saiman.experiment.production.ProductPath;
import uk.co.saiman.experiment.production.Output;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentResultCell {
  public static final String ID = "uk.co.saiman.experiment.msapex.result.cell";

  @Inject
  private Observation<?> observation;

  @Inject
  @Optional
  public void prepare(MCell cell, ExperimentPath<?> path, Output results) {
    /*
     * configure label
     */
    results
        .resolveResult(ProductPath.define(path, observation))
        .value()
        .ifPresentOrElse(
            result -> cell
                .setIconURI(
                    "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/document-binary.png"),
            () -> cell
                .setIconURI(
                    "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/document.png"));
  }
}
