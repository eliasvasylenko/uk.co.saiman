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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.REMOVE_STEP;

import java.util.Optional;

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ProductPath;

public class RemoveStepEvent extends ExperimentStepEvent {
  private final  Optional<ProductPath<Absolute, ?>> previousDependencyPath;
  private final Optional<Step> previousDependencyStep;

  public RemoveStepEvent(Step step, Optional<Step> previousParent) {
    super(step);
    this.previousDependencyPath = step.getDependencyPath();
    this.previousDependencyStep = previousParent;
  }

  @Override
  public ExperimentEventKind kind() {
    return REMOVE_STEP;
  }

  public Optional<ProductPath<Absolute, ?>> previousDependencyPath() {
    return previousDependencyPath;
  }

  public Optional<Step> previousDependencyStep() {
    return previousDependencyStep;
  }
}
