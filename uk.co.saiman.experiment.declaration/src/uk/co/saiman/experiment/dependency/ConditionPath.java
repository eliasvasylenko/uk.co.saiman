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
 * This file is part of uk.co.saiman.experiment.declaration.
 *
 * uk.co.saiman.experiment.declaration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.declaration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.dependency;

import java.util.Optional;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class ConditionPath<T extends ExperimentPath<T>, U> extends ProductPath<T, Condition<U>> {
  ConditionPath(ExperimentPath<T> experimentPath, Class<U> production) {
    super(experimentPath, production);
  }

  @SuppressWarnings("unchecked")
  public Class<U> getPreparation() {
    return (Class<U>) getProduction();
  }

  @Override
  <V extends ExperimentPath<V>> ConditionPath<V, U> moveTo(ExperimentPath<V> experimentPath) {
    return new ConditionPath<>(experimentPath, getPreparation());
  }

  @Override
  public Optional<ConditionPath<Absolute, U>> resolveAgainst(ExperimentPath<Absolute> path) {
    return getExperimentPath().resolveAgainst(path).map(experimentPath -> moveTo(experimentPath));
  }

  @Override
  public ConditionPath<Absolute, U> toAbsolute() {
    return moveTo(getExperimentPath().toAbsolute());
  }
}
