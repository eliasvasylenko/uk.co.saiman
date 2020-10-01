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
 * This file is part of uk.co.saiman.experiment.conductor.
 *
 * uk.co.saiman.experiment.conductor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.conductor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.ExecutionCancelledException;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.json.JsonProcedureFormat;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.state.json.JsonStateMapFormat;

public class Execution {
  private final Conductor conductor;
  private final ConductorOutput output;
  private final WorkspaceExperimentPath path;

  private final Instruction instruction;
  private final Environment environment;
  private final OutgoingConditions outgoingConditions;
  private final OutgoingResults outgoingResults;
  private final IncomingDependencies incomingDependencies;

  private Location location;
  private Future<?> executionThread;
  private boolean valid = true;
  private boolean completed = false;

  public Execution(
      ConductorOutput output,
      WorkspaceExperimentPath path,
      Instruction instruction,
      Environment environment,
      OutgoingConditions outgoingConditions,
      OutgoingResults outgoingResults,
      IncomingDependencies incomingDependencies) {
    this.conductor = output.getConductor();
    this.output = output;
    this.path = path;

    this.instruction = instruction;
    this.environment = environment;
    this.outgoingConditions = outgoingConditions;
    this.outgoingResults = outgoingResults;
    this.incomingDependencies = incomingDependencies;
  }

  private ExecutionContext createExecutionContext(Consumer<Resource<?>> acquiredResource) {
    return new ExecutionContext() {
      @Override
      public ExperimentId getId() {
        return instruction.id();
      }

      @Override
      public WorkspaceExperimentPath getPath() {
        return path;
      }

      @Override
      public Location getLocation() {
        return location;
      }

      @Override
      public Variables getVariables() {
        return new Variables(environment, instruction.variableMap());
      }

      @Override
      public <T> Resource<T> acquireResource(Class<T> source) {
        conductor.lock().lock();
        try {
          var resource = environment.provideResource(source);
          acquiredResource.accept(resource);
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
      public <U> void prepareCondition(Class<U> condition, Supplier<? extends U> resource) {
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
  }

  void run() {
    var acquiredResources = new HashSet<Resource<?>>();

    try {
      var executionContext = createExecutionContext(acquiredResources::add);

      executionContext.useOnce(instruction.executor());
    } catch (Exception e) {
      conductor.log().log(Level.ERROR, e);
      throw e;

    } finally {
      completeRun(acquiredResources);
    }
  }

  private void completeRun(Set<Resource<?>> acquiredResources) {
    try {
      conductor.lock().lock();

      completed = true;

      outgoingConditions.terminate();
      outgoingResults.terminate();
      incomingDependencies.terminate();

      acquiredResources.stream().flatMap(r -> {
        try {
          r.close();
          return Stream.empty();
        } catch (RuntimeException e) {
          return Stream.of(e);
        }
      }).reduce((e1, e2) -> {
        e1.addSuppressed(e2);
        return e1;
      }).ifPresent(r -> {
        throw r;
      });

    } catch (Exception e) {
      conductor.log().log(Level.ERROR, e);
      throw e;

    } finally {
      conductor.lock().unlock();
    }
  }

  public boolean isRunComplete() {
    return completed;
  }

  void start() {
    try {
      prepareLocation();
      executionThread = conductor.getExecutor().submit(this::run);

    } catch (Exception e) {
      outgoingConditions.terminate();
      outgoingResults.terminate();
      incomingDependencies.terminate();

      conductor.log().log(Level.ERROR, "Failed to start experiment.", e);
    }
  }

  void stop() {
    try {
      if (executionThread != null) {
        try {
          executionThread.cancel(true);
          executionThread.get();
        } catch (ExecutionCancelledException | InterruptedException e) {}
      }

      clearLocation();

    } catch (Exception e) {
      conductor.log().log(Level.ERROR, "Failed to stop experiment.", e);
    }
  }

  void join() {
    if (executionThread != null) {
      try {
        executionThread.get();
      } catch (Exception e) {}
    }
  }

  private void prepareLocation() throws IOException {
    try {
      conductor.lock().lock();

      location = conductor.storageConfiguration().locateStorage(path).location();
      var procedureFormat = new JsonProcedureFormat(conductor.executorService(), environment, new JsonStateMapFormat());
      var data = Data.locate(location, instruction.id().name(), procedureFormat);
      data.set(instruction.extractMinimalProcedure());
      data.save();

    } finally {
      conductor.lock().unlock();
    }
  }

  private void clearLocation() throws IOException {
    try {
      conductor.lock().lock();

      var re = location.resources().flatMap(r -> {
        try {
          r.delete();
          return Stream.empty();
        } catch (IOException e) {
          return Stream.of(e);
        }
      }).reduce((e1, e2) -> {
        e1.addSuppressed(e2);
        return e1;
      }).orElse(null);
      if (re != null) {
        throw re;
      }

    } finally {
      location = null;
      conductor.lock().unlock();
    }
  }

  boolean isValid() {
    return valid;
  }

  void invalidate() {
    if (valid) {
      valid = false;
      incomingDependencies.invalidate();
      outgoingConditions.invalidate();
      outgoingResults.invalidate();

      stop();
    }
  }
}
