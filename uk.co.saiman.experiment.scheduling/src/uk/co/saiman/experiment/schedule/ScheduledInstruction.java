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
 * This file is part of uk.co.saiman.experiment.scheduling.
 *
 * uk.co.saiman.experiment.scheduling is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.scheduling is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.schedule;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.variables.Variables;

public class ScheduledInstruction {
  private final Schedule schedule;
  private final Instruction instruction;
  private final Optional<ProductPath<Absolute>> path;

  ScheduledInstruction(
      Schedule schedule,
      Instruction instruction,
      Optional<ProductPath<Absolute>> path) {
    this.schedule = schedule;
    this.instruction = instruction;
    this.path = path;
  }

  public Instruction instruction() {
    return instruction;
  }

  public Optional<ProductPath<Absolute>> productPath() {
    return path;
  }

  public ExperimentPath<Absolute> experimentPath() {
    return path
        .map(ProductPath::getExperimentPath)
        .orElse(ExperimentPath.defineAbsolute())
        .resolve(instruction.id());
  }

  public String id() {
    return instruction.id();
  }

  public Conductor<?> conductor() {
    return instruction.conductor();
  }

  public Variables variables() {
    return instruction.variables();
  }

  public Optional<ScheduledInstruction> parent() {
    return schedule.getParent(this);
  }

  public Stream<ScheduledInstruction> children() {
    return schedule.getChildren(this);
  }
}
