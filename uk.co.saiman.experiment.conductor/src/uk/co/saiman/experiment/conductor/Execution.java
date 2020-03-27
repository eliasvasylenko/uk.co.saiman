package uk.co.saiman.experiment.conductor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.executor.ExecutionCancelledException;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class Execution {
  private final Conductor conductor;
  private final WorkspaceExperimentPath path;

  private final Instruction instruction;
  private final LocalEnvironment environment;
  private final OutgoingConditions outgoingConditions;
  private final OutgoingResults outgoingResults;
  private final IncomingDependencies incomingDependencies;

  private Location location;
  private Thread executionThread;
  private boolean completed = false;

  public Execution(
      Conductor conductor,
      WorkspaceExperimentPath path,
      Instruction instruction,
      LocalEnvironment environment,
      OutgoingConditions outgoingConditions,
      OutgoingResults outgoingResults,
      IncomingDependencies incomingDependencies) {
    this.conductor = conductor;
    this.path = path;

    this.instruction = instruction;
    this.environment = environment;
    this.outgoingConditions = outgoingConditions;
    this.outgoingResults = outgoingResults;
    this.incomingDependencies = incomingDependencies;

    this.executionThread = new Thread(this::run);
  }

  private ExecutionContext createExecutionContext(Consumer<Resource<?>> acquiredResource) {
    return new ExecutionContext() {
      @Override
      public ExperimentId getId() {
        return instruction.id();
      }

      @Override
      public Location getLocation() {
        return location;
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
  }

  void run() {
    Set<Resource<?>> acquiredResources = new HashSet<Resource<?>>();

    try {
      var executionContext = createExecutionContext(acquiredResources::add);

      executionContext.useOnce(instruction.executor());
    } finally {
      try {
        conductor.lock().lock();

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

      } finally {
        try {
          completed = true;
          conductor.completeExecution(this);

        } finally {
          conductor.lock().unlock();
        }
      }
    }
  }

  public boolean isRunComplete() {
    return completed;
  }

  void start() {
    try {
      prepareLocation();
      executionThread.start();

    } catch (Exception e) {
      outgoingConditions.terminate();
      outgoingResults.terminate();
      incomingDependencies.terminate();

      conductor.log().log(Level.ERROR, "Failed to start experiment.", e);
    }
  }

  void stop() {
    try {
      try {
        executionThread.interrupt();
        executionThread.join();
      } catch (ExecutionCancelledException | InterruptedException e) {}

      outgoingConditions.terminate();
      outgoingResults.terminate();
      incomingDependencies.terminate();

      clearLocation();

    } catch (Exception e) {
      conductor.log().log(Level.ERROR, "Failed to stop experiment.", e);
    }
  }

  private void prepareLocation() throws IOException {
    try {
      conductor.lock().lock();

      location = conductor.storageConfiguration().locateStorage(path).location();
      var data = Data.locate(location, instruction.id().name(), conductor.instructionFormat());
      data.set(instruction);
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
}
