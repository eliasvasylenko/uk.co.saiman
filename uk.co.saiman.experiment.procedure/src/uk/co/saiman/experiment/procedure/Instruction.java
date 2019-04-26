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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Dependency;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.Variables;

public class Instruction<T extends Dependency> extends InstructionContainer<Instruction<T>> {
  private final String id;
  private final Variables variables;
  private final Conductor<T> conductor;

  private Instruction(
      String id,
      Variables variables,
      Conductor<T> conductor,
      List<Instruction<?>> instructions) {
    super(instructions);
    this.id = id;
    this.variables = variables;
    this.conductor = conductor;
  }

  private Instruction(
      String id,
      Variables variables,
      Conductor<T> conductor,
      List<Instruction<?>> instructions,
      Map<String, Instruction<?>> dependents) {
    super(instructions, dependents);
    this.id = id;
    this.variables = variables;
    this.conductor = conductor;
  }

  public static <T extends Dependency> Instruction<T> define(
      String id,
      Variables variables,
      Conductor<T> conductor) {
    return new Instruction<>(
        Procedure.validateName(id),
        requireNonNull(variables),
        requireNonNull(conductor),
        List.of(),
        Map.of());
  }

  public String id() {
    return id;
  }

  public Instruction<T> withId(String id) {
    return new Instruction<>(
        Procedure.validateName(id),
        variables,
        conductor,
        getInstructions(),
        getDependents());
  }

  public Variables variables() {
    return variables;
  }

  public Instruction<T> withVariables(Variables variables) {
    return new Instruction<>(id, variables, conductor, getInstructions(), getDependents());
  }

  public <U> Optional<U> variable(Variable<U> variable) {
    return variables.get(variable);
  }

  public <U> Instruction<T> withVariable(Variable<U> variable, U value) {
    return withVariables(variables.with(variable, value));
  }

  public <U> Instruction<T> withVariable(Variable<U> variable, Function<Optional<U>, U> value) {
    return withVariables(variables.with(variable, value));
  }

  public Conductor<T> conductor() {
    return conductor;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;

    Instruction<?> that = (Instruction<?>) obj;

    return Objects.equals(this.id, that.id)
        && Objects.equals(this.variables, that.variables)
        && Objects.equals(this.conductor, that.conductor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, variables, conductor, super.hashCode());
  }

  @Override
  Instruction<T> with(List<Instruction<?>> instructions, Map<String, Instruction<?>> dependents) {
    return new Instruction<>(id, variables, conductor, instructions, dependents);
  }

  @Override
  Instruction<T> with(List<Instruction<?>> instructions) {
    return new Instruction<>(id, variables, conductor, instructions);
  }

  @SuppressWarnings("unchecked")
  public <U extends Product> Stream<Instruction<U>> dependentInstructions(
      Production<U> production) {
    return instructions()
        .filter(i -> i.conductor().directRequirement().equals(Requirement.on(production)))
        .map(i -> (Instruction<U>) i);
  }
}
