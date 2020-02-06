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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.instruction.Instruction;

public class ProcedureDependents {
  enum DependencyKind {
    CONDITION, RESULT, ADDITIONAL_RESULT, ORDERING
  }

  static class Dependency {
    private final DependencyKind kind;
    private final Class<?> production;
    private final ExperimentPath<Absolute> from;
    private final ExperimentPath<Absolute> to;

    public Dependency(
        DependencyKind kind,
        Class<?> production,
        ExperimentPath<Absolute> from,
        ExperimentPath<Absolute> to) {
      this.kind = kind;
      this.production = production;
      this.from = from;
      this.to = to;
    }

    public DependencyKind kind() {
      return kind;
    }

    public Class<?> production() {
      return production;
    }

    public ExperimentPath<Absolute> from() {
      return from;
    }

    public ExperimentPath<Absolute> to() {
      return to;
    }
  }

  private final Map<ExperimentPath<Absolute>, InstructionDependents> instructionDependencies;

  ProcedureDependents(Procedure procedure) {
    instructionDependencies = new HashMap<>();
    procedure
        .instructions()
        .forEach(instruction -> addInstruction(procedure.environment(), instruction));
  }

  public InstructionDependents getInstructionDependents(ExperimentPath<Absolute> path) {
    return instructionDependencies.getOrDefault(path, InstructionDependents.empty());
  }

  InstructionDependents updateInstructionDependents(
      ExperimentPath<Absolute> path,
      Function<InstructionDependents, InstructionDependents> action) {
    return instructionDependencies.put(path, action.apply(getInstructionDependents(path)));
  }

  static InstructionDependents withDependent(
      InstructionDependents dependencies,
      Dependency dependency) {
    switch (dependency.kind()) {
    case CONDITION:
      return dependencies.withConditionDependent(dependency.production(), dependency.from());
    case RESULT:
    case ADDITIONAL_RESULT:
      return dependencies.withResultDependent(dependency.production(), dependency.from());
    default:
      throw new AssertionError();
    }
  }

  void addInstruction(GlobalEnvironment environment, Instruction instruction) {
    Procedures.plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void declareResultRequirement(Class<?> production) {
        instruction.path().parent().ifPresent(dependency -> {
          addDependency(
              new Dependency(DependencyKind.RESULT, production, instruction.path(), dependency));
        });
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        instruction.path().parent().ifPresent(dependency -> {
          addDependency(
              new Dependency(DependencyKind.CONDITION, production, instruction.path(), dependency));
          
          var parent = instructionDependencies.get(dependency);
          if (parent != null) {
            // TODO add ordering dependencies iff the condition is declared on the parent as ORDERED
            parent.
          }
        });
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        path.getExperimentPath().resolveAgainst(instruction.path()).ifPresent(dependency -> {
          addDependency(
              new Dependency(
                  DependencyKind.ADDITIONAL_RESULT,
                  path.getProduction(),
                  instruction.path(),
                  dependency));
        });
      }
    });
  }

  void addDependency(Dependency dependency) {
    updateInstructionDependents(dependency.to(), d -> withDependent(d, dependency));
  }
}
