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
package uk.co.saiman.experiment;

import java.util.Optional;

import uk.co.saiman.experiment.path.ResultPath;

/**
 * An input is a wiring from a dependency to an observation which satisfies that
 * dependency.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the dependency
 */
public class Input<T> {
  private final ExperimentStep<?> step;
  private final Dependency<T> dependency;
  private ResultPath<T> resultPath;

  public Input(ExperimentStep<?> step, Dependency<T> dependency) {
    this.step = step;
    this.dependency = dependency;
  }

  public ExperimentStep<?> getExperimentStep() {
    return step;
  }

  public Dependency<T> getDependency() {
    return dependency;
  }

  public void setResultPath(ResultPath<T> resultPath) {
    this.resultPath = resultPath;
  }

  public ResultPath<T> getResultPath() {
    return resultPath;
  }

  public Optional<Result<? extends T>> getResult() {
    return resultPath.resolve(step);
  }
}
