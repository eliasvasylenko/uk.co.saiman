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

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.Type;

import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.reflection.token.TypeToken;

public interface MissingExperimentType<T> extends ExperimentType<StateMap, T> {
  @Override
  default Type getThisType() {
    return MissingExperimentType.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  default TypeToken<T> getResultType() {
    /*
     * TODO best effort at result type by loading any persisted result data and
     * using the type of that?
     */
    return (TypeToken<T>) forType(void.class);
  }

  @Override
  default StateMap createState(ConfigurationContext<StateMap> context) {
    return context.state();
  }

  @Override
  default TypeToken<StateMap> getStateType() {
    return new TypeToken<StateMap>() {};
  }

  @Override
  default boolean mayComeAfter(ExperimentType<?, ?> parentType) {
    return true;
  }
}
