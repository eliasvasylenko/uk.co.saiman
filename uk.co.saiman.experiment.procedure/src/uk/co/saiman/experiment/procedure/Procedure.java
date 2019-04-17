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

import java.util.Map;
import java.util.NavigableMap;

import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Nothing;

public class Procedure extends Instructions<Procedure, Absolute> {
  private final String id;

  private Procedure(String id) {
    this.id = validateName(id);
  }

  private Procedure(
      String id,
      NavigableMap<ExperimentPath<Absolute>, ExperimentLocation> instructions,
      Map<ProductPath<Absolute>, ProductLocation> dependencies) {
    super(instructions, dependencies);
    this.id = validateName(id);
  }

  public static Procedure define(String id) {
    return new Procedure(id);
  }

  public String id() {
    return id;
  }

  public Procedure withId(String id) {
    return new Procedure(id, getInstructions(), getDependencies());
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
  protected Procedure withInstructions(
      NavigableMap<ExperimentPath<Absolute>, ExperimentLocation> instructions,
      Map<ProductPath<Absolute>, ProductLocation> dependencies) {
    return new Procedure(id, instructions, dependencies);
  }

  public Procedure withInstruction(Instruction instruction) {
    return withInstruction(-1, instruction);
  }

  @Override
  Procedure withInstruction(long index, Instruction instruction) {
    return super.withInstruction(index, instruction);
  }

  public Procedure withTemplate(Template<Nothing> template) {
    return withTemplate(-1, template);
  }

  @Override
  public Procedure withTemplate(long index, Template<Nothing> template) {
    return super.withTemplate(index, template);
  }

  @Override
  ExperimentPath<Absolute> getExperimentPath() {
    return ExperimentPath.defineAbsolute();
  }
}
