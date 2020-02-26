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
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.Dependency.Kind;

public class ProcedureDependencies {
  private final Map<ExperimentPath<Absolute>, InstructionDependencies> instructionDependencies;

  ProcedureDependencies(Procedure procedure) {
    instructionDependencies = new HashMap<>();
    procedure
        .instructions()
        .forEach(instruction -> addInstruction(procedure.environment(), procedure, instruction));
  }

  public InstructionDependencies getInstructionDependents(ExperimentPath<Absolute> path) {
    return instructionDependencies.computeIfAbsent(path, InstructionDependencies::new);
  }

  InstructionDependencies updateInstructionDependents(
      ExperimentPath<Absolute> path,
      Function<InstructionDependencies, InstructionDependencies> action) {
    return instructionDependencies.put(path, action.apply(getInstructionDependents(path)));
  }

  private void addInstruction(
      GlobalEnvironment environment,
      Procedure procedure,
      Instruction instruction) {
    Procedures.plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        updateInstructionDependents(instruction.path(), d -> d.withEvaluation(type, evaluation));
      }

      @Override
      public void declareResultRequirement(Class<?> production) {
        instruction.path().parent().ifPresent(dependency -> {
          addDependency(new Dependency(Kind.RESULT, production, instruction.path(), dependency));
        });
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        instruction.path().parent().ifPresent(dependency -> {
          var parent = getInstructionDependents(dependency);
          /*
           * If the condition we're consuming is ordered, we must declare an
           * additional ordering dependency on the the last instruction to
           * consume that condition.
           */
          parent
              .getEvaluation(production)
              .filter(Evaluation.ORDERED::equals)
              .ifPresent(
                  e -> parent
                      .getDependenciesTo()
                      .filter(d -> d.kind() == Kind.CONDITION && d.production() == production)
                      .reduce((a, b) -> b)
                      .map(Dependency::from)
                      .ifPresent(
                          orderingDependency -> addDependency(
                              new Dependency(
                                  Kind.ORDERING,
                                  production,
                                  instruction.path(),
                                  orderingDependency))));

          addDependency(new Dependency(Kind.CONDITION, production, instruction.path(), dependency));
        });
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        path.getExperimentPath().resolveAgainst(instruction.path()).ifPresent(dependency -> {
          addDependency(
              new Dependency(
                  Kind.ADDITIONAL_RESULT,
                  path.getProduction(),
                  instruction.path(),
                  dependency));
        });
      }
    });
  }

  void addDependency(Dependency dependency) {
    updateInstructionDependents(dependency.to(), d -> d.withDependency(dependency));
    updateInstructionDependents(dependency.from(), d -> d.withDependency(dependency));
  }
}
