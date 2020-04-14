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

import static uk.co.saiman.experiment.declaration.ExperimentPath.toRoot;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

/**
 * An experiment procedure is the machine-consumable bundle of information to
 * describe how a experiment should be conducted.
 * <p>
 * An experiment procedure describes a complete experiment graph, where each
 * node is described by its constituent {@link #instructions() instructions}.
 * <p>
 * Generally procedures should not be generated by hand, as the API provides
 * little protection against invalid configurations and is cumbersome for direct
 * use. Rather, they are intended as a target for higher-level APIs to emit in
 * order to prepare and direct experiment execution.
 * 
 * @author Elias N Vasylenko
 *
 */
public class Procedure {
  private final ExperimentId id;
  private final LinkedHashMap<WorkspaceExperimentPath, Instruction> instructions;
  private final Environment environment;

  public Procedure(
      ExperimentId id,
      Collection<? extends Instruction> instructions,
      Environment environment) {
    this.id = id;
    this.instructions = new LinkedHashMap<>();
    for (var instruction : instructions) {
      this.instructions.put(WorkspaceExperimentPath.define(id, instruction.path()), instruction);
    }
    this.environment = environment;
  }

  public ExperimentId id() {
    return id;
  }

  public Stream<Instruction> instructions() {
    return instructions.values().stream();
  }

  public Environment environment() {
    return environment;
  }

  public WorkspaceExperimentPath path() {
    return WorkspaceExperimentPath.define(id, toRoot());
  }

  public Stream<WorkspaceExperimentPath> instructionPaths() {
    return instructions.keySet().stream();
  }

  public Optional<Instruction> instruction(WorkspaceExperimentPath path) {
    return Optional.ofNullable(instructions.get(path));
  }

  public Optional<Instruction> instruction(ExperimentPath<?> path) {
    return instruction(WorkspaceExperimentPath.define(id, path.toAbsolute()));
  }
}
