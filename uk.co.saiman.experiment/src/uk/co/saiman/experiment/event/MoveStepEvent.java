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

import java.util.Optional;

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentId;

public class MoveStepEvent extends ExperimentStepEvent {
  private final ExperimentId previousId;
  private final Optional<Step> previousDependencyStep;
  private final Optional<Step> dependencyStep;

  public MoveStepEvent(Step step, Optional<Step> previousParent, ExperimentId previousId) {
    super(step);
    this.previousId = previousId;
    this.previousDependencyStep = previousParent;
    this.dependencyStep = step.getSuperstep();
  }

  @Override
  public ExperimentEventKind kind() {
    return ExperimentEventKind.MOVE_STEP;
  }

  public ExperimentId previousId() {
    return previousId;
  }

  public ExperimentId id() {
    return stepDesign().id();
  }

  public Optional<Step> previousDependencyStep() {
    return previousDependencyStep;
  }

  public Optional<Step> dependencyStep() {
    return dependencyStep;
  }
}
