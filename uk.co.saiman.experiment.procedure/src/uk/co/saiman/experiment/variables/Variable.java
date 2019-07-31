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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import uk.co.saiman.experiment.environment.SharedEnvironment;
import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.State;

/**
 * A variable is simply a representation of an API point. In particular, it
 * represents a type of {@link State state} which may be associated with an
 * instruction, and the Java type which the state may be materialized as.
 * <p>
 * Variable instances are intended to be static.
 */
public class Variable<T> {
  private final String id;
  private final Function<? super SharedEnvironment, ? extends Accessor<T, ?>> accessor;

  public Variable(String id, Accessor<T, ?> accessor) {
    this.id = requireNonNull(id);
    this.accessor = requireNonNull(e -> accessor);
  }

  public Variable(
      String id,
      Function<? super SharedEnvironment, ? extends Accessor<T, ?>> accessor) {
    this.id = requireNonNull(id);
    this.accessor = requireNonNull(accessor);
  }

  public String id() {
    return id;
  }

  public MapIndex<T> mapIndex(SharedEnvironment environment) {
    return new MapIndex<>(id, accessor.apply(environment));
  }

  public VariableDeclaration declareRequired() {
    return declare(VariableCardinality.REQUIRED);
  }

  public VariableDeclaration declareOptional() {
    return declare(VariableCardinality.OPTIONAL);
  }

  public VariableDeclaration declare(VariableCardinality cardinality) {
    return new VariableDeclaration(this, cardinality);
  }
}
