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
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.experiment.procedure.Dependency.Kind.ADDITIONAL_RESULT;
import static uk.co.saiman.experiment.procedure.Dependency.Kind.CONDITION;
import static uk.co.saiman.experiment.procedure.Dependency.Kind.ORDERING;
import static uk.co.saiman.experiment.procedure.Dependency.Kind.RESULT;

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
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.executor.ExecutionCancelledException;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.Dependency.Kind;
import uk.co.saiman.experiment.procedure.Dependency;
import uk.co.saiman.experiment.procedure.InstructionDependencies;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.log.Log;

public class InstructionExecution {
  /*
   * Configured
   */
  private final Conductor conductor;
  private final java.util.concurrent.locks.Condition dependencyReady;

  private Instruction instruction;
  private InstructionDependencies dependencies;
  private LocalEnvironment environment;

  private final ConditionPreparations conditionPreparations;
  private final ResultObservations resultObservations;

  private Thread executionThread;

  public InstructionExecution(Conductor conductor) {
    this.conductor = conductor;
    this.conditionPreparations = new ConditionPreparations(conductor.lock(), () -> dependencies);
    this.resultObservations = new ResultObservations(conductor.lock(), () -> dependencies);
  }

  public Conductor getConductor() {
    return conductor;
  }

  InstructionExecution update(
      Instruction instruction,
      InstructionDependencies dependencies,
      LocalEnvironment environment) {
    if (this.instruction != null && !this.instruction.path().equals(instruction.path())) {
      throw new IllegalStateException("This shouldn't happen!");
    }

    var previousDependencies = this.dependencies;
    var previousInstruction = this.instruction;

    if (!isCompatibleConfiguration(instruction, previousInstruction) || !isCompatibleOrdering()) {
      interrupt();
    }

    this.dependencies = requireNonNull(dependencies);
    this.instruction = requireNonNull(instruction);
    this.environment = requireNonNull(environment);

    return this;
  }

  private boolean isCompatibleConfiguration(
      Instruction instruction,
      Instruction previousInstruction) {
    return previousInstruction != null && (previousInstruction.id().equals(instruction.id())
        && previousInstruction.executor().equals(instruction.executor())
        && previousInstruction.variableMap().equals(instruction.variableMap()));
  }

  private boolean isCompatibleOrdering() {
    /*
     * 
     * TODO
     * 
     * for UNORDERED/ORDERED if the consumer is invalidated...
     * 
     * or for ORDERED if there is a new consumer who's ordering puts it before
     * someone who has already consumed the condition ...
     * 
     * or for ORDERED if existing completed consumers switch order
     * 
     * the performer must be invalidated.
     * 
     * 
     * 
     */
    // TODO Auto-generated method stub
    return false;
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
    System.out
        .println(" - dependencies to this: " + dependencies.getDependenciesTo().collect(toList()));
    System.out
        .println(
            " - dependencies from this: " + dependencies.getDependenciesTo().collect(toList()));

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
        conductor.lock().lock();
        try {
          do {
            var orderingDependency = dependencies
                .getDependenciesFrom()
                .filter(d -> d.production() == source && d.kind() == ORDERING)
                .map(Dependency::to)
                .map(conductor::findInstruction)
                .flatMap(Optional::stream)
                .reduce((a, b) -> b)
                .orElse(null);

            var conditionDependency = dependencies
                .getDependenciesFrom()
                .filter(d -> d.production() == source && d.kind() == CONDITION)
                .map(Dependency::to)
                .map(conductor::findInstruction)
                .flatMap(Optional::stream)
                .reduce((a, b) -> b)
                .orElseThrow(
                    () -> new ConductorException(
                        format(
                            "Failed to acquire condition %s for instruction %s",
                            source,
                            instruction.path())));

            if (orderingDependency == null || orderingDependency.isDone().consumedConditions
                .get(source) == conditionDependency) {
              return conditionDependency.conditionPreparations
                  .consume(source, InstructionExecution.this);
            }
            dependencyReady.await();
          } while (true);
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <T> Resource<T> acquireResource(Class<T> source) {
        System.out.println("     -> -> ->");
        conductor.lock().lock();
        try {
          System.out.println("        -> -> -> !!!!");
          return environment.provideResource(source);
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <T> Result<T> acquireResult(Class<T> source) {
        conductor.lock().lock();
        try {
          var dependency = dependencies
              .getDependenciesFrom()
              .filter(d -> d.production() == source && d.kind() == RESULT)
              .map(Dependency::to)
              .map(conductor::findInstruction)
              .flatMap(Optional::stream)
              .reduce((a, b) -> b)
              .orElseThrow(
                  () -> new ConductorException(
                      format(
                          "Failed to acquire result %s for instruction %s",
                          source,
                          instruction.path())));

          return dependency.consumeResult(source);
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <T> Stream<Result<T>> acquireAdditionalResults(Class<T> source) {
        conductor.lock().lock();
        try {
          return dependencies
              .getDependenciesFrom()
              .filter(d -> d.production() == source && d.kind() == ADDITIONAL_RESULT)
              .map(Dependency::to)
              .map(conductor::findInstruction)
              .flatMap(Optional::stream)
              .map(dependency -> dependency.consumeResult(source));
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <U> void prepareCondition(Class<U> condition, U resource) {
        conditionPreparations.prepare(condition, resource);

        var conditionDependents = dependencies
            .getConditionDependents(condition)
            .collect(toCollection(HashSet::new));

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        conductor.lock().lock();
        try {
          var conditionDependents = dependencies
              .getConditionDependents(condition)
              .collect(toCollection(HashSet::new));
          System.out.println("prepare condition for " + conditionDependents);
          if (conditionDependents.isEmpty()) {
            return;
          }
          try {
            preparedCondition = condition;
            preparedConditionValue = resource;
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
            preparedConditionValue = null;
          }
        } finally {
          conductor.lock().unlock();
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
        conductor.lock().lock();
        try {
          executionThread = null;
        } finally {
          consumedConditions.values().forEach(Condition::close);
          conductor.lock().unlock();
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
        conductor.lock().newCondition().
      } catch (InterruptedException e) {
        throw new ExecutionCancelledException(e);
      }
    }
    return new ResultImpl<>(source, instruction.path());
  }

  void interruptDependency(Dependency dependency) {
    boolean invalidate;

    switch (dependency.kind()) {
    case RESULT:
    case ADDITIONAL_RESULT:
      invalidate = Optional
          .ofNullable(consumedResults.get(dependency.production()))
          .stream()
          .flatMap(Set::stream)
          .map(r -> r.getExecution().instruction.path())
          .anyMatch(dependency.to()::equals);
      break;
    case CONDITION:
      invalidate = Optional
          .ofNullable(consumedConditions.get(dependency.production()))
          .map(c -> c.getExecution().instruction.path())
          .filter(dependency.to()::equals)
          .isPresent();
      break;
    case ORDERING:
      invalidate = consumedConditions.containsKey(dependency.production());
      break;
    default:
      throw new AssertionError();
    }

    if (invalidate) {
      interrupt();
    }
  }

  void interrupt() {
    conditionPreparations.consumers().forEach(InstructionExecution::interrupt);
    resultObservations.consumers().forEach(InstructionExecution::interrupt);

    if (executionThread != null) {
      executionThread.interrupt();

      try {
        executionThread.join();

      } catch (ExecutionCancelledException | InterruptedException e) {} finally {
        executionThread = null;
      }
    }
  }
}
