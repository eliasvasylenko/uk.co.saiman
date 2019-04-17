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

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Relative;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.Variables;

public class Template<T extends Product> extends Instructions<Template<T>, Relative> {
  private final String id;
  private final Variables variables;
  private final Conductor<T> conductor;

  private Template(String id, Variables variables, Conductor<T> conductor) {
    this.id = id;
    this.variables = variables;
    this.conductor = conductor;
  }

  private Template(
      String id,
      Variables variables,
      Conductor<T> conductor,
      NavigableMap<ExperimentPath<Relative>, ExperimentLocation> instructions,
      Map<ProductPath<Relative>, ProductLocation> dependencies) {
    super(instructions, dependencies);
    this.id = id;
    this.variables = variables;
    this.conductor = conductor;
  }

  public static <S, T extends Product> Template<T> define(
      String id,
      Variables variables,
      Conductor<T> conductor) {
    return new Template<>(Procedure.validateName(id), variables, conductor);
  }

  public String id() {
    return id;
  }

  public Template<T> withId(String id) {
    return new Template<>(id, variables, conductor, getInstructions(), getDependencies());
  }

  public Variables variables() {
    return variables;
  }

  public Template<T> withVariables(Variables variables) {
    return new Template<>(id, variables, conductor);
  }

  public <U> Optional<U> variable(Variable<U> variable) {
    return variables.get(variable);
  }

  public <U> Template<T> withVariable(Variable<U> variable, U value) {
    return withVariables(variables.with(variable, value));
  }

  public <U> Template<T> withVariable(Variable<U> variable, Function<Optional<U>, U> value) {
    return withVariables(variables.with(variable, value));
  }

  public Conductor<T> conductor() {
    return conductor;
  }

  public Instruction instruction() {
    return Instruction.define(id, variables, conductor);
  }

  @Override
  protected Template<T> withInstructions(
      NavigableMap<ExperimentPath<Relative>, ExperimentLocation> instructions,
      Map<ProductPath<Relative>, ProductLocation> dependencies) {
    return new Template<>(id, variables, conductor, instructions, dependencies);
  }

  @Override
  ExperimentPath<Relative> getExperimentPath() {
    return ExperimentPath.defineRelative();
  }
}
