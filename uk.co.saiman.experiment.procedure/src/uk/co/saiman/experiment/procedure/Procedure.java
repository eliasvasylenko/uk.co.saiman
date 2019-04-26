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

import static java.lang.String.format;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Nothing;

public class Procedure extends InstructionContainer<Procedure> {
  private final String id;

  private Procedure(String id, List<Instruction<?>> instructions) {
    super(instructions);
    this.id = validateName(id);
  }

  private Procedure(
      String id,
      List<Instruction<?>> instructions,
      Map<String, Instruction<?>> dependents) {
    super(instructions, dependents);
    this.id = validateName(id);
  }

  public static Procedure define(String id) {
    return new Procedure(id, List.of(), Map.of());
  }

  public String id() {
    return id;
  }

  public Procedure withId(String id) {
    return new Procedure(id, getInstructions(), getDependents());
  }

  static String validateName(String name) {
    if (!isNameValid(name)) {
      throw new ProcedureException(format("Invalid name for experiment %s", name));
    }
    return name;
  }

  public static boolean isNameValid(String name) {
    final String ALPHANUMERIC = "[a-zA-Z0-9]+";
    final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

    return name != null
        && name.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*");
  }

  @Override
  Procedure with(List<Instruction<?>> instructions, Map<String, Instruction<?>> dependents) {
    return new Procedure(id, instructions, dependents);
  }

  @Override
  Procedure with(List<Instruction<?>> instructions) {
    return new Procedure(id, instructions);
  }

  @SuppressWarnings("unchecked")
  public Stream<Instruction<Nothing>> independentInstructions() {
    return instructions()
        .filter(i -> i.conductor().directRequirement().equals(Requirement.none()))
        .map(i -> (Instruction<Nothing>) i);
  }
}
