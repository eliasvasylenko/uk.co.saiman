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
package uk.co.saiman.experiment.procedure;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.Variables;

public class Instruction {
  private final String id;
  private final Variables variables;
  private final Conductor<?> conductor;

  private Instruction(String id, Variables variables, Conductor<?> conductor) {
    this.id = id;
    this.variables = variables;
    this.conductor = conductor;
  }

  public static Instruction define(String id, Variables variables, Conductor<?> conductor) {
    return new Instruction(
        Procedure.validateName(id),
        requireNonNull(variables),
        requireNonNull(conductor));
  }

  public String id() {
    return id;
  }

  public Instruction withId(String id) {
    return new Instruction(Procedure.validateName(id), variables, conductor);
  }

  public Variables variables() {
    return variables;
  }

  public Instruction withVariables(Variables variables) {
    return new Instruction(id, variables, conductor);
  }

  public <T> Optional<T> variable(Variable<T> variable) {
    return variables.get(variable);
  }

  public <U> Instruction withVariable(Variable<U> variable, U value) {
    return withVariables(variables.with(variable, value));
  }

  public <U> Instruction withVariable(Variable<U> variable, Function<Optional<U>, U> value) {
    return withVariables(variables.with(variable, value));
  }

  public Conductor<?> conductor() {
    return conductor;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    Instruction that = (Instruction) obj;

    return Objects.equals(this.id, that.id)
        && Objects.equals(this.variables, that.variables)
        && Objects.equals(this.conductor, that.conductor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, variables, conductor);
  }
}
