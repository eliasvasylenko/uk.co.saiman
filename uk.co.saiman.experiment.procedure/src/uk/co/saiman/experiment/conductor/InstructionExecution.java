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
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.executor.Evaluation.INDEPENDENT;

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
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.executor.ExecutionCancelledException;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.InstructionDependents;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.log.Log;

public class InstructionExecution {
  /*
   * Configured
   */
  private final Conductor conductor;

  private Instruction instruction;
  private InstructionDependents dependencies;
  private LocalEnvironment environment;

  private Class<?> preparedCondition;
  private ConditionImpl<?> preparedConditionValue;
  private final Set<Class<?>> preparedConditions = new HashSet<>();
  private final Map<Class<?>, ResultImpl<?>> observedResults = new HashMap<>();

  private final Map<Class<?>, InstructionExecution> consumedConditions = new HashMap<>();
  private final Map<Class<?>, Set<InstructionExecution>> consumedResults = new HashMap<>();

  private Thread executionThread;

  public InstructionExecution(Conductor conductor) {
    this.conductor = conductor;
  }

  public Conductor getConductor() {
    return conductor;
  }

  InstructionExecution update(
      Instruction instruction,
      InstructionDependents dependencies,
      LocalEnvironment environment) {
    if (this.instruction != null && !this.instruction.path().equals(instruction.path())) {
      throw new IllegalStateException("This shouldn't happen!");
    }

    if (this.instruction != null) {
      if (!isCompatibleConfiguration(instruction)) {
        interrupt();
        dependencies
            .getResultDependents()
            .forEach(
                result -> dependencies
                    .getResultDependents(result)
                    .map(conductor::findInstruction)
                    .flatMap(Optional::stream)
                    .filter(dependent -> dependent.consumedResults.get(result).contains(this))
                    .forEach(InstructionExecution::interrupt));
        dependencies
            .getConditionDependents()
            .forEach(
                condition -> dependencies
                    .getConditionDependents(condition)
                    .map(conductor::findInstruction)
                    .flatMap(Optional::stream)
                    .filter(dependent -> dependent.consumedConditions.get(condition).equals(this))
                    .forEach(InstructionExecution::interrupt));
      }
    }

    this.instruction = requireNonNull(instruction);
    this.dependencies = requireNonNull(dependencies);
    this.environment = requireNonNull(environment);

    return this;
  }

  private boolean isCompatibleConfiguration(Instruction instruction) {
    return this.instruction.id().equals(instruction.id())
        && this.instruction.executor().equals(instruction.executor())
        && this.instruction.variableMap().equals(instruction.variableMap());
  }

  void execute() {
    /*
     * 
     * TODO detect if already conducting and whether dependencies have changed
     * and continue/interrupt as appropriate
     * 
     */

    System.out.println("Executing");
    System.out.println(" - instruction: " + instruction.id());
    System.out.println(" - conditions: " + dependencies.getConditionDependents().collect(toList()));
    System.out.println(" - results " + dependencies.getResultDependents().collect(toList()));

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
        return new Variables(environment.getGlobalEnvironment(), instruction.variableMap());
      }

      @Override
      public <T> Condition<T> acquireCondition(Class<T> source) {
        synchronized (conductor) {
          var dependency = Procedures
              .getConditionDependency(instruction, source)
              .flatMap(conductor::findInstruction)
              .orElseThrow(
                  () -> new ConductorException(
                      format(
                          "Failed to acquire condition %s for instruction %s",
                          source,
                          instruction.path())));

          System.out.println(" £££££££££££££");
          System.out.println(source);
          System.out.println(Procedures.getConditionDependency(instruction, source));

          return dependency.consumeCondition(source, InstructionExecution.this);
        }
      }

      @Override
      public <T> Resource<T> acquireResource(Class<T> source) {
        System.out.println("     -> -> ->");
        synchronized (conductor) {
          System.out.println("        -> -> -> !!!!");
          return environment.provideResource(source);
        }
      }

      @Override
      public <T> Result<T> acquireResult(Class<T> source) {
        synchronized (conductor) {
          var dependency = Procedures
              .getResultDependency(instruction, source)
              .flatMap(conductor::findInstruction)
              .orElseThrow(
                  () -> new ConductorException(
                      format(
                          "Failed to acquire result %s for instruction %s",
                          source,
                          instruction.path())));

          return dependency.consumeResult(source);
        }
      }

      @Override
      public <T> Stream<Result<T>> acquireAdditionalResults(Class<T> source) {
        synchronized (conductor) {
          return Procedures
              .getAdditionalResultDependencies(instruction, source)
              .map(conductor::findInstruction)
              .flatMap(Optional::stream)
              .map(dependency -> dependency.consumeResult(source));
        }
      }

      @Override
      public <U> void prepareCondition(Class<U> condition, U resource) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        synchronized (conductor) {
          var conditionDependents = dependencies
              .getConditionDependents(condition)
              .collect(toCollection(HashSet::new));
          System.out.println("prepare condition for " + conditionDependents);
          if (conditionDependents.isEmpty()) {
            return;
          }
          try {
            preparedCondition = condition;
            preparedConditionValue = new ConditionImpl<>(condition, instruction.path(), resource);
            do {
              System.out.println("prepare condition for " + conditionDependents);
              conductor.notifyAll();
              conductor.wait();
            } while (dependencies.getConditionDependents(condition).findAny().isPresent());
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } finally {
            preparedConditions.add(condition);
            preparedCondition = null;
            preparedConditionValue.close();
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

    System.out.println("  __running");
    executionThread.start();
  }

  boolean isRunning() {
    return executionThread != null;
  }

  protected <T> Result<T> consumeResult(Class<T> source) {
    while (!observedResults.containsKey(source)) {
      if (!isRunning()) {
        throw new ConductorException(
            format("Failed to acquire result %s from instruction %s", source, instruction.path()));
      }
      try {
        conductor.wait();
      } catch (InterruptedException e) {
        throw new ExecutionCancelledException(e);
      }
    }
    return new ResultImpl<>(source, instruction.path());
  }

  @SuppressWarnings("unchecked")
  protected <T> Condition<T> consumeCondition(Class<T> source, InstructionExecution consumer) {
    var evaluation = Procedures.getPreparedConditionEvaluation(instruction, source);

    while (source != preparedCondition
        && (evaluation == INDEPENDENT || evaluation == Evaluation.PARALLEL)) {
      if (preparedConditions.contains(source) || !isRunning()) {
        throw new ConductorException(
            format(
                "Failed to acquire condition %s from instruction %s",
                source,
                instruction.path()));
      }
      try {
        System.out.println("await condition " + source);
        System.out.println("prepared " + preparedCondition);
        System.out.println("prepared in " + instruction.path());
        conductor.wait();
      } catch (InterruptedException e) {
        throw new ExecutionCancelledException(e);
      }
    }
    return (Condition<T>) preparedConditionValue;
  }

  void interrupt() {
    if (executionThread != null) {
      executionThread.interrupt();
      try {
        executionThread.join();
      } catch (ExecutionCancelledException | InterruptedException e) {}
      executionThread = null;
    }
  }
}
