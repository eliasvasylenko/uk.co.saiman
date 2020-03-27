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
package uk.co.saiman.experiment.schedule.conflicts;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.data.resource.Resource;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.InstructionPlanningContext;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.schedule.Schedule;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

public class Conflicts {
  private final Schedule schedule;
  private final GlobalEnvironment environment;
  private final Map<WorkspaceExperimentPath, Change> differences;

  public Conflicts(Schedule schedule, GlobalEnvironment environment) {
    this.schedule = schedule;
    this.environment = environment;

    this.differences = new HashMap<>();
    schedule
        .getScheduledProcedure()
        .stream()
        .flatMap(Procedure::instructionPaths)
        .forEach(this::checkDifference);
    schedule
        .getPreviouslyConductedProcedure()
        .stream()
        .flatMap(Procedure::instructionPaths)
        .forEach(this::checkDifference);
  }

  public boolean isConflictFree() {
    return changes().allMatch(not(Change::isConflicting));
  }

  public Stream<Change> changes() {
    return differences.values().stream();
  }

  public Optional<Change> change(WorkspaceExperimentPath path) {
    return Optional.ofNullable(differences.get(path));
  }

  private void checkDifference(WorkspaceExperimentPath experimentPath) {
    if (!differences.containsKey(experimentPath)) {
      differences.put(experimentPath, new ChangeImpl(experimentPath));
    }
  }

  public class ChangeImpl implements Change {
    private final WorkspaceExperimentPath path;

    private final Optional<Instruction> previousInstruction;
    private final Optional<Instruction> scheduledInstruction;

    public ChangeImpl(WorkspaceExperimentPath path) {
      this.path = path;

      this.previousInstruction = schedule
          .getPreviouslyConductedProcedure()
          .flatMap(p -> p.instruction(path.getExperimentPath()));
      this.scheduledInstruction = schedule
          .getScheduledProcedure()
          .flatMap(p -> p.instruction(path.getExperimentPath()));
    }

    @Override
    public WorkspaceExperimentPath path() {
      return path;
    }

    @Override
    public Optional<Instruction> currentInstruction() {
      return previousInstruction;
    }

    @Override
    public Optional<Instruction> scheduledInstruction() {
      return scheduledInstruction;
    }

    @Override
    public boolean isConflicting() {
      try {
        return conflictingInstruction().isPresent() || conflictingResources().findAny().isPresent()
            || conflictingDependencies().findAny().isPresent();
      } catch (IOException e) {
        return true;
      }
    }

    @Override
    public Stream<Resource> conflictingResources() throws IOException {
      return currentInstruction().isPresent()
          ? schedule
              .getScheduler()
              .getStorageConfiguration()
              .locateStorage(path)
              .location()
              .resources()
          : Stream.empty();
    }

    @Override
    public Optional<Instruction> conflictingInstruction() {
      return previousInstruction
          .filter(state -> scheduledInstruction.filter(state::equals).isEmpty());
    }

    @Override
    public Stream<Change> conflictingDependencies() {
      return scheduledInstruction
          .stream()
          .flatMap(this::resolveDependencies)
          .collect(toMap(Change::path, identity(), (a, b) -> a))
          .values()
          .stream();
    }

    private Stream<Change> resolveDependencies(Instruction scheduledInstruction) {
      List<ExperimentPath<Absolute>> dependencies = new ArrayList<>();

      Procedures
          .plan(scheduledInstruction, environment, variables -> new InstructionPlanningContext() {
            @Override
            public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
              path
                  .getExperimentPath()
                  .resolveAgainst(scheduledInstruction.path())
                  .ifPresent(dependencies::add);
            }

            public void declareConditionRequirement(java.lang.Class<?> production) {
              scheduledInstruction.path().parent().ifPresent(dependencies::add);
            }

            public void declareResultRequirement(java.lang.Class<?> production) {
              scheduledInstruction.path().parent().ifPresent(dependencies::add);
            }
          });

      return dependencies
          .stream()
          .flatMap(path -> path.resolveAgainst(scheduledInstruction.path()).stream())
          .flatMap(path -> conflictingDependency(path).stream());
    }

    private Optional<Change> conflictingDependency(ExperimentPath<Absolute> path) {
      return change(WorkspaceExperimentPath.define(this.path.getExperimentId(), path))
          .filter(c -> c.isConflicting());
    }
  }
}
