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

import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * An observation can be made during an {@link ExperimentStep experiment step}
 * to produce a {@link Result result}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the result we wish to find
 */
public abstract class Observation<T> {
  private final String id;

  public Observation(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }

  public Type getThisType() {
    return getClass();
  }

  public TypeToken<T> getResultType() {
    return forType(getThisType())
        .resolveSupertype(Procedure.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }

  public TypeToken<Observation<T>> getThisTypeToken() {
    return new TypeToken<Observation<T>>() {}
        .withTypeArguments(new TypeArgument<T>(getResultType()) {});
  }

  public TypedReference<Observation<T>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }
}
