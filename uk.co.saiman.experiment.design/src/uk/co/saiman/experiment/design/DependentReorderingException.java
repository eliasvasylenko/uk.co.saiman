/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.definition.
 *
 * uk.co.saiman.experiment.definition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.definition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.design;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Relative;

public class DependentReorderingException extends ExperimentDesignException {
  private static final long serialVersionUID = 1L;

  private final ExperimentPath<Relative> stepPath;
  private final ExperimentId substepA;
  private final ExperimentId substepB;

  public DependentReorderingException(ExperimentPath<Relative> stepPath, ExperimentId substepA, ExperimentId substepB) {
    super(
        "Cannot override shared method with substeps '" + substepA + "' and '" + substepB + "' out of order at '"
            + stepPath + "'");
    this.stepPath = stepPath;
    this.substepA = substepA;
    this.substepB = substepB;
  }

  public ExperimentPath<Relative> getStepPath() {
    return stepPath;
  }

  public ExperimentId getSubtepA() {
    return substepA;
  }

  public ExperimentId getSubtepB() {
    return substepB;
  }
}