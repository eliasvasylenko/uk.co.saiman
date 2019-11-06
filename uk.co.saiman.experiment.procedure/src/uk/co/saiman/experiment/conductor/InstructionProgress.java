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
package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.requirement.ProductPath;
import uk.co.saiman.experiment.requirement.Production;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.log.Log;

public class InstructionProgress {
  private final Conductor conductor;

  private Instruction instruction;
  private GlobalEnvironment environment;

  private Variables variables;

  private final Map<Production<?, ?>, Set<InstructionProgress>> dependents;

  public InstructionProgress(
      Conductor conductor,
      Instruction instruction,
      GlobalEnvironment environment) {
    this.conductor = conductor;
    this.instruction = instruction;
    this.environment = environment;
    this.dependents = new HashMap<>();
  }

  public Conductor getConductor() {
    return conductor;
  }

  public Instruction getInstruction() {
    return instruction;
  }

  public GlobalEnvironment getEnvironment() {
    return environment;
  }

  void update(Instruction instruction, GlobalEnvironment environment) {
    if (!this.instruction.path().equals(instruction.path())) {
      throw new IllegalStateException("This shouldn't happen!");
    }

    this.instruction = instruction;
    this.environment = environment;
  }

  void begin() {
    prepare();
    conductor.execute(this::conduct);
  }

  void interrupt() {

  }

  protected void addDependent(Production<?, ?> production, InstructionProgress dependent) {
    dependents.computeIfAbsent(production, p -> new HashSet<>()).add(dependent);
  }

  protected Stream<InstructionProgress> getDependents(Production<?, ?> production) {
    return dependents.getOrDefault(production, Set.of()).stream();
  }

  /**
   * Wire up our dependencies. If requirements appear to be unavailable we don't
   * throw here, we still make a best-effort attempt to execute each instruction.
   * The way to deal with broken invariants like that is by reporting errors and
   * warnings in the UI.
   */
  private void prepare() {
    variables = new Variables(environment, instruction.variableMap());

    var context = new PlanningContext.NoOpPlanningContext() {
      private boolean live;

      void complete() {
        live = false;
      }

      private void assertLive() {
        if (!live) {
          throw new IllegalStateException();
        }
      }

      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        assertLive();
        return variables.get(declaration.variable());
      }

      @Override
      public void declareMainRequirement(Production<?, ?> production) {
        assertLive();

        instruction.path().parent().flatMap(conductor::findInstruction).ifPresent(dependency -> {
          dependency.addDependent(production, InstructionProgress.this);
        });
      }

      @Override
      public void declareAdditionalRequirement(ProductPath<?, ?> path) {
        assertLive();

        path
            .getExperimentPath()
            .resolveAgainst(instruction.path())
            .flatMap(conductor::findInstruction)
            .ifPresent(dependency -> {
              dependency.addDependent(path.getProduction(), InstructionProgress.this);
            });
      }
    };
    instruction.executor().plan(context);
    context.complete();
  }

  private void conduct() {
    /*
     * 
     * TODO detect if already conducting and whether dependencies have changed and
     * continue/interrupt as appropriate
     * 
     */
    var context = new ExecutionContext() {
      private boolean live;

      void complete() {
        live = false;
      }

      private void assertLive() {
        if (!live) {
          throw new IllegalStateException();
        }
      }

      @Override
      public ExperimentId getId() {
        assertLive();
        return instruction.id();
      }

      @Override
      public Location getLocation() {
        assertLive();
        try {
          return conductor.storageConfiguration().locateStorage(instruction.path()).location();
        } catch (IOException e) {
          throw new ConductorException(
              format("Failed to allocate storage for %s", instruction.path()));
        }
      }

      @Override
      public Variables getVariables() {
        assertLive();
        return new Variables(environment, instruction.variableMap());
      }

      @Override
      public <T> Condition<T> acquireCondition(Class<T> source) {
        assertLive();
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Resource<T> acquireResource(Class<T> source) {
        assertLive();
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Result<T> acquireResult(Class<T> source) {
        assertLive();
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Stream<Result<T>> acquireResults(Class<T> source) {
        assertLive();
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <U> void prepareCondition(Class<U> condition, U resource) {
        assertLive();
        // TODO Auto-generated method stub

      }

      @Override
      public <R> void observePartialResult(Class<R> observation, Supplier<? extends R> value) {
        assertLive();
        // TODO Auto-generated method stub

      }

      @Override
      public void completeObservation(Class<?> observation) {
        assertLive();
        // TODO Auto-generated method stub

      }

      @Override
      public <R> void setResultData(Class<R> observation, Data<R> data) {
        assertLive();
        // TODO Auto-generated method stub

      }

      @Override
      public Log log() {
        assertLive();
        return conductor.log();
      }
    };
    instruction.executor().execute(context);
    context.complete();
  }
}
