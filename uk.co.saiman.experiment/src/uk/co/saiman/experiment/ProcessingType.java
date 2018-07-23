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

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

public interface ProcessingType<S, T, U> extends ExperimentType<S, U> {
  @Override
  default boolean hasAutomaticExecution() {
    return true;
  }

  @Override
  default boolean isProcessingContextDependent() {
    return false;
  }

  @Override
  default boolean mayComeAfter(ExperimentType<?, ?> parentType) {
    return parentType.getResultType().isAssignableTo(getInputType());
  }

  @Override
  default U process(ProcessingContext<S, U> context) {
    @SuppressWarnings("unchecked")
    T input = (T) context.node().getParent().flatMap(p -> p.getResult().getValue()).orElse(null);
    return process(context.node().getState(), input);
  }

  U process(S state, T input);

  /**
   * @return the exact generic type of the input of this processing step
   */
  default TypeToken<T> getInputType() {
    return forType(getThisType())
        .resolveSupertype(ProcessingType.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }
}
