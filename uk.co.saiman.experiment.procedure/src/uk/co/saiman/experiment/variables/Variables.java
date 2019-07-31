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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.variables;

import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.experiment.environment.SharedEnvironment;
import uk.co.saiman.state.StateMap;

/**
 * An immutable container for experiment procedure variables.
 * 
 * @author Elias N Vasylenko
 */
public class Variables {
  private final SharedEnvironment environment;
  private final StateMap state;

  public Variables(SharedEnvironment environment) {
    this.environment = environment;
    this.state = StateMap.empty();
  }

  public Variables(SharedEnvironment environment, StateMap state) {
    this.environment = environment;
    this.state = state;
  }

  public StateMap state() {
    return state;
  }

  public <T> Optional<T> get(Variable<T> variable) {
    return state.getOptional(variable.mapIndex(environment));
  }

  public <U> Variables with(Variable<U> variable, U value) {
    return new Variables(environment, state.with(variable.mapIndex(environment), value));
  }

  public <U> Variables with(
      Variable<U> variable,
      Function<? super Optional<U>, ? extends U> value) {
    return new Variables(
        environment,
        state.with(variable.mapIndex(environment), value.apply(get(variable))));
  }
}
