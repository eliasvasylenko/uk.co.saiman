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

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * An input to an experiment procedure should be wired up to an observation made
 * by a preceding procedure.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the result we wish to find
 */
public abstract class ResultRequirement<T> extends Requirement<Result<T>> {
  public TypeToken<T> getResultType() {
    return forType(getClass())
        .resolveSupertype(Procedure.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Observation<T>> resolveCapability(Capability<?> capability) {
    return capability instanceof Observation<?>
        && ((Observation<?>) capability).getResultType().isAssignableTo(getResultType())
            ? Optional.of((Observation<T>) capability)
            : Optional.empty();
  }

  @Override
  public Stream<Observation<T>> resolveCapabilities(Procedure<?> procedure) {
    return procedure.observations().map(this::resolveCapability).flatMap(Optional::stream);
  }
}
