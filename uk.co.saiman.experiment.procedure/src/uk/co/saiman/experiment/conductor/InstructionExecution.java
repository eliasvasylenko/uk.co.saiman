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
import static uk.co.saiman.experiment.conductor.InstructionExecution.UpdateStatus.DIRTY;
import static uk.co.saiman.experiment.conductor.InstructionExecution.UpdateStatus.INVALID;
import static uk.co.saiman.experiment.conductor.InstructionExecution.UpdateStatus.VALID;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.conductor.OutgoingResults.ResultObservation.IncomingResult;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.executor.ExecutionCancelledException;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.log.Log;

public class InstructionExecution {
  /**
   * Instructions can be updated mid-execution. This is made thread safe by
   * locking on any interaction with their context during the update. This
   * represents the status of an update in progress.
   * 
   * @author Elias N Vasylenko
   */
  enum UpdateStatus {
    /**
     * The update has been made and appears to be valid. External factors may still
     * mark this as invalid. If the status is valid when execution resumes, it can
     * resume safely.
     */
    VALID,
    /**
     * The update has been flagged as invalidating. If the status is invalid when
     * execution resumes, it must terminate and restart.
     */
    INVALID,
    /**
     * The update has not been made yet. If the status is dirty when execution
     * resumes, this indicates that the execution is no longer needed and should
     * terminate.
     */
    DIRTY
  }

  /*
   * Conductor
   */
  private final Conductor conductor;
  private final ExperimentPath<Absolute> path;

  /*
   * Configuration
   */
  private Instruction instruction;
  private LocalEnvironment environment;

  /*
   * Dependencies
   */
  private UpdateStatus updateStatus;
  private final OutgoingConditions outgoingConditions;
  private final OutgoingResults outgoingResults;
  private final IncomingDependencies incomingDependencies;

  private Thread executionThread;

  public InstructionExecution(Conductor conductor, ExperimentPath<Absolute> path) {
    this.conductor = conductor;
    this.path = path;

    this.outgoingConditions = new OutgoingConditions(conductor.lock(), path);
    this.outgoingResults = new OutgoingResults(conductor.lock());
    this.incomingDependencies = new IncomingDependencies(conductor, path);

    markDirty();
  }

  public Instruction getInstruction() {
    return instruction;
  }

  public Conductor getConductor() {
    return conductor;
  }

  void markDirty() {
    updateStatus = DIRTY;

    outgoingConditions.invalidate();
    outgoingResults.invalidate();
  }

  InstructionExecution update(Instruction instruction, LocalEnvironment environment) {
    requireNonNull(instruction);
    requireNonNull(environment);

    if (!this.path.equals(instruction.path())) {
      throw new IllegalStateException("This shouldn't happen!");
    }

    outgoingConditions.update(instruction, environment);
    incomingDependencies.update(instruction, environment);

    updateStatus = isCompatibleConfiguration(instruction, this.instruction) ? VALID : INVALID;

    this.instruction = instruction;
    this.environment = environment;

    return this;
  }

  protected <T> IncomingCondition<T> addConditionConsumer(Class<T> type) {
    if (updateStatus != DIRTY) {
      return outgoingConditions
          .getOutgoingCondition(type)
          .map(c -> c.addConsumer(path))
          .orElseThrow(
              () -> new ConductorException(
                  "Cannot add dependency on missing condition " + type + " to " + path));
    }
    throw new ConductorException(
        "Cannot add forward dependency on condition " + type + " to " + path);
  }

  protected <T> IncomingResult<T> addResultConsumer(Class<T> type) {
    if (updateStatus != DIRTY) {
      return outgoingResults.addConsumer(type);
    }
    throw new ConductorException("Cannot add forward dependency on result " + type + " to " + path);
  }

  private boolean isCompatibleConfiguration(
      Instruction instruction,
      Instruction previousInstruction) {
    return previousInstruction != null
        && (previousInstruction.id().equals(instruction.id())
            && previousInstruction.executor().equals(instruction.executor())
            && previousInstruction.variableMap().equals(instruction.variableMap()));
  }

  boolean execute() {
    if (updateStatus == DIRTY) {
      interrupt();
      return false;
    }
    if (updateStatus == INVALID) {
      interrupt();
    }

    /*
     * 
     * TODO detect if already conducting and whether dependencies have changed and
     * continue/interrupt as appropriate
     * 
     */

    Set<Resource<?>> acquiredResources = new HashSet<>();

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
      public <T> Resource<T> acquireResource(Class<T> source) {
        conductor.lock().lock();
        try {
          var resource = environment.provideResource(source);
          acquiredResources.add(resource);
          return resource;
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <T> Condition<T> acquireCondition(Class<T> source) {
        conductor.lock().lock();
        try {
          return incomingDependencies.acquireCondition(source);
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <T> Result<T> acquireResult(Class<T> source) {
        conductor.lock().lock();
        try {
          return incomingDependencies.acquireResult(source);
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <T> Stream<Result<T>> acquireAdditionalResults(Class<T> source) {
        conductor.lock().lock();
        try {
          return incomingDependencies.acquireAdditionalResults(source);
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <U> void prepareCondition(Class<U> condition, U resource) {
        conductor.lock().lock();
        try {
          outgoingConditions.getOutgoingCondition(condition).ifPresent(o -> o.prepare(resource));
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <R> void observePartialResult(Class<R> observation, Supplier<? extends R> value) {
        conductor.lock().lock();
        try {
          // TODO Auto-generated method stub
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public void completeObservation(Class<?> observation) {
        conductor.lock().lock();
        try {
          // TODO Auto-generated method stub
        } finally {
          conductor.lock().unlock();
        }
      }

      @Override
      public <R> void setResultData(Class<R> observation, Data<R> data) {
        conductor.lock().lock();
        try {
          // TODO Auto-generated method stub
        } finally {
          conductor.lock().unlock();
        }
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
          outgoingConditions.terminate();
          outgoingResults.terminate();
          incomingDependencies.terminate();

          acquiredResources.forEach(Resource::close);
        } finally {
          conductor.lock().unlock();
        }
      }
    });

    executionThread.start();

    return true;
  }

  boolean isRunning() {
    return executionThread != null;
  }

  void interrupt() {
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
