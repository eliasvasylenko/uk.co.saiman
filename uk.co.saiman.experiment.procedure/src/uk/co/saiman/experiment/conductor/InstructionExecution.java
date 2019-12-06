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
import static java.util.stream.Collectors.toCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.log.Log;

public class InstructionExecution {
  /*
   * Configured
   */
  private final Conductor conductor;

  private Instruction instruction;
  private InstructionDependents dependents;
  private GlobalEnvironment environment;

  private Class<?> preparedCondition;
  private Object preparedConditionValue;

  private final Map<Class<?>, InstructionExecution> conditionDependenciesConsumed = new HashMap<>();
  private final Map<Class<?>, Set<InstructionExecution>> resultDependenciesConsumed = new HashMap<>();

  private Thread executionThread;

  public InstructionExecution(Conductor conductor) {
    this.conductor = conductor;
  }

  public Conductor getConductor() {
    return conductor;
  }

  InstructionExecution update(
      Instruction instruction,
      InstructionDependents dependents,
      GlobalEnvironment environment) {
    if (this.instruction != null && !this.instruction.path().equals(instruction.path())) {
      throw new IllegalStateException("This shouldn't happen!");
    }

    if (this.instruction != null) {
      if (!this.instruction.id().equals(instruction.id())
          || !this.instruction.executor().equals(instruction.executor())
          || !this.instruction.variableMap().equals(instruction.variableMap())) {
        interrupt();
        dependents
            .getConsumedResults()
            .forEach(
                result -> dependents
                    .getResultDependents(result)
                    .map(conductor::findInstruction)
                    .flatMap(Optional::stream)
                    .filter(dependent -> dependent.resultDependenciesConsumed.containsKey(result))
                    .forEach(InstructionExecution::interrupt));
        dependents
            .getConsumedConditions()
            .forEach(
                condition -> dependents
                    .getConditionDependents(condition)
                    .map(conductor::findInstruction)
                    .flatMap(Optional::stream)
                    .filter(
                        dependent -> dependent.conditionDependenciesConsumed.containsKey(condition))
                    .forEach(InstructionExecution::interrupt));
      }
    }

    this.instruction = instruction;
    this.dependents = dependents;
    this.environment = environment;

    return this;
  }

  void execute() {
    /*
     * 
     * TODO detect if already conducting and whether dependencies have changed and
     * continue/interrupt as appropriate
     * 
     */

    var executionContext = new ExecutionContext() {
      @Override
      public ExperimentId getId() {
        return instruction.id();
      }

      @Override
      public Location getLocation() {
        try {
          return conductor.storageConfiguration().locateStorage(instruction.path()).location();
        } catch (IOException e) {
          throw new ConductorException(
              format("Failed to allocate storage for %s", instruction.path()));
        }
      }

      @Override
      public Variables getVariables() {
        return new Variables(environment, instruction.variableMap());
      }

      @Override
      public <T> Condition<T> acquireCondition(Class<T> source) {
        synchronized (conductor) {
          return null;
        }
      }

      @Override
      public <T> Resource<T> acquireResource(Class<T> source) {
        synchronized (conductor) {
          return null;
        }
      }

      @Override
      public <T> Result<T> acquireResult(Class<T> source) {
        synchronized (conductor) {
          return null;
        }
      }

      @Override
      public <T> Stream<Result<T>> acquireResults(Class<T> source) {
        synchronized (conductor) {
          return null;
        }
      }

      @Override
      public <U> void prepareCondition(Class<U> condition, U resource) {
        synchronized (conductor) {
          var conditionDependents = dependents
              .getConditionDependents(condition)
              .collect(toCollection(HashSet::new));
          if (conditionDependents.isEmpty()) {
            return;
          }
          try {
            preparedCondition = condition;
            preparedConditionValue = resource;
            do {
              conductor.notifyAll();
              conductor.wait();
            } while (!conditionDependents.isEmpty());
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } finally {
            preparedCondition = null;
            preparedConditionValue = null;
          }
        }
      }

      @Override
      public <R> void observePartialResult(Class<R> observation, Supplier<? extends R> value) {
        // TODO Auto-generated method stub

      }

      @Override
      public void completeObservation(Class<?> observation) {
        // TODO Auto-generated method stub

      }

      @Override
      public <R> void setResultData(Class<R> observation, Data<R> data) {
        // TODO Auto-generated method stub

      }

      @Override
      public Log log() {
        return conductor.log();
      }
    };

    executionThread = new Thread(() -> {
      try {
        executionContext.useOnce(instruction.executor());
      } finally {
        synchronized (conductor) {
          executionThread = null;
        }
      }
    });

    executionThread.run();
  }

  void interrupt() {
    if (executionThread != null) {
      executionThread.interrupt();
      try {
        executionThread.join();
      } catch (InterruptedException e) {}
      executionThread = null;
    }
  }
}
