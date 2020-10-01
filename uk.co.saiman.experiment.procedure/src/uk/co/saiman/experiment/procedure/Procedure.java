/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.experiment.declaration.ExperimentPath.toRoot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.state.StateMap;

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
  private final Environment environment;
  private final Map<WorkspaceExperimentPath, Instruction> instructions;

  Procedure(
      ExperimentId id,
      Environment environment,
      LinkedHashMap<WorkspaceExperimentPath, Instruction> instructions) {
    this.id = id;
    this.environment = environment;
    this.instructions = unmodifiableMap(instructions);
  }

  public static Procedure empty(ExperimentId id, Environment environment) {
    return new Procedure(id, environment, new LinkedHashMap<>());
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

  public Procedure withInstruction(ExperimentPath<Absolute> path, StateMap variableMap, Executor executor) {
    var instructions = new LinkedHashMap<>(this.instructions);
    instructions.put(WorkspaceExperimentPath.define(id, path), new Instruction(this, path, variableMap, executor));
    return new Procedure(id, environment, instructions);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    Procedure that = (Procedure) obj;

    return Objects.equals(this.id, that.id) && Objects.equals(this.environment, that.environment)
        && Objects.equals(this.instructions.keySet(), that.instructions.keySet())
        && instructions.keySet().stream().allMatch(path -> {
          var thisInstruction = this.instruction(path).get();
          var thatInstruction = that.instruction(path).get();
          return Objects.equals(thisInstruction.variableMap(), thatInstruction.variableMap())
              && Objects.equals(thisInstruction.executor(), thatInstruction.executor());
        });
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(
            id,
            environment,
            List.copyOf(instructions.keySet()),
            instructions.values().stream().map(Instruction::variableMap).collect(toSet()),
            instructions.values().stream().map(Instruction::executor).collect(toSet()));
  }
}
