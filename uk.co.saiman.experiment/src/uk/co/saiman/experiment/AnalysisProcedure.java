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

import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

public interface AnalysisProcedure<S, T, U> extends Procedure<S> {
  Dependency<T> input();

  Observation<U> output();

  @Override
  default Stream<Condition> preparedConditions() {
    return Stream.empty();
  }

  @Override
  default Stream<Condition> requiredConditions() {
    return Stream.empty();
  }

  @Override
  default Stream<Dependency<?>> dependencies() {
    return Stream.of(input());
  }

  @Override
  default Stream<Observation<?>> observations() {
    return Stream.of(output());
  }

  @Override
  default boolean hasAutomaticExecution() {
    return true;
  }

  default boolean hasPartialExecution() {
    return false;
  }

  @Override
  default void proceed(ProcedureContext<S> context) {
    T input = (T) context.resolveInput(input()).getValue().orElse(null);
    U output = process(context.node().getVariables(), input);
    context.setResult(output(), output);
  }

  U process(S state, T input);

  /**
   * @return the exact generic type of the input of this processing step
   */
  default TypeToken<T> getInputType() {
    return forType(getThisType())
        .resolveSupertype(AnalysisProcedure.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }
}
