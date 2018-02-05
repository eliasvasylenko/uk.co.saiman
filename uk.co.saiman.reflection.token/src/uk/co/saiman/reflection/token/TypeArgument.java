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
 * This file is part of uk.co.saiman.reflection.token.
 *
 * uk.co.saiman.reflection.token is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection.token is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection.token;

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import uk.co.saiman.reflection.TypeHierarchy;

public abstract class TypeArgument<T> {
  private final TypeParameter<T> parameter;
  private final TypeToken<T> type;

  public TypeArgument(TypeToken<T> type) {
    this.parameter = resolveSupertypeParameter();
    this.type = type;
  }

  public TypeArgument(Class<T> type) {
    this.parameter = resolveSupertypeParameter();
    this.type = forType(type);
  }

  protected TypeArgument(TypeParameter<T> parameter, TypeToken<T> type) {
    this.parameter = parameter;
    this.type = type;
  }

  @SuppressWarnings("unchecked")
  private TypeParameter<T> resolveSupertypeParameter() {
    Type type = ((ParameterizedType) new TypeHierarchy(getClass().getGenericSuperclass())
        .resolveSupertype(TypeArgument.class)).getActualTypeArguments()[0];

    if (!(type instanceof TypeVariable<?>))
      throw new IllegalArgumentException();

    return (TypeParameter<T>) TypeParameter.forTypeVariable((TypeVariable<?>) type);
  }

  public TypeParameter<T> getParameter() {
    return parameter;
  }

  public TypeToken<T> getTypeToken() {
    return type;
  }

  public Type getType() {
    return type.getType();
  }
}
