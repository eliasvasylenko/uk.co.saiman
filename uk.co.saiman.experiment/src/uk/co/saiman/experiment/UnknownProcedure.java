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

import static java.lang.String.format;
import static uk.co.saiman.reflection.token.TypeToken.forType;

import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.reflection.token.TypeToken;

public class UnknownProcedure<T> implements ExperimentProcedure<StateMap, T> {
  private final String id;

  public UnknownProcedure(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @SuppressWarnings("unchecked")
  @Override
  public TypeToken<T> getResultType() {
    /*
     * TODO best effort at result type by loading any persisted result data and
     * using the type of that. This way we can still load the results and use
     * child-nodes for e.g. analysis.
     */
    return (TypeToken<T>) forType(void.class);
  }

  @Override
  public StateMap configureVariables(ConfigurationContext<StateMap> context) {
    return context.stateMap();
  }

  @Override
  public TypeToken<StateMap> getVariablesType() {
    return new TypeToken<StateMap>() {};
  }

  @Override
  public boolean mayComeAfter(ExperimentProcedure<?, ?> parentType) {
    return true;
  }

  @Override
  public T process(ProcessingContext<StateMap, T> context) {
    throw new ExperimentException(format("Cannot execute missing experiment type %s", id));
  }
}
