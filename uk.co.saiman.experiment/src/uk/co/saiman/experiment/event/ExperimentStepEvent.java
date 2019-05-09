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

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.graph.ExperimentPath;
import uk.co.saiman.experiment.graph.ExperimentPath.Absolute;

public abstract class ExperimentStepEvent extends ExperimentEvent {
  private final Step step;
  private final ExperimentPath<Absolute> path;

  public ExperimentStepEvent(Step step) {
    super(step.getExperiment());
    this.step = step;
    this.path = step.getPath();
  }

  @Override
  public abstract ExperimentEventKind kind();

  public ExperimentPath<Absolute> path() {
    return path;
  }

  public StepDefinition<?> stepDefinition() {
    return experimentDefinition().findSubstep(path).get();
  }

  public Step step() {
    return step;
  }
}
