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

public class Execution {
  private final Conductor conductor;
  private final WorkspaceExperimentPath path;

  private final Instruction instruction;
  private final LocalEnvironment environment;
  private final OutgoingConditions outgoingConditions;
  private final OutgoingResults outgoingResults;
  private final IncomingDependencies incomingDependencies;

  private final Thread executionThread;

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

  private Location getLocation() throws IOException {
    return conductor.storageConfiguration().locateStorage(path).location();
  }

  private ExecutionContext createExecutionContext(Consumer<Resource<?>> acquiredResource) {
    return new ExecutionContext() {
      @Override
      public ExperimentId getId() {
        return instruction.id();
      }

      @Override
      public Location getLocation() {
        return getLocation();
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

        acquiredResources.forEach(Resource::close);
      } finally {
        conductor.lock().unlock();
      }
    }
  }

  void start() throws IOException {
    try {
      prepareLocation();
    } catch (Exception e) {
      outgoingConditions.terminate();
      outgoingResults.terminate();
      incomingDependencies.terminate();

      throw e;
    }

    executionThread.start();
  }

  void stop() throws IOException {
    try {
      executionThread.interrupt();
      executionThread.join();

    } catch (ExecutionCancelledException | InterruptedException e) {

    } finally {
      outgoingConditions.terminate();
      outgoingResults.terminate();
      incomingDependencies.terminate();

      clearLocation();
    }
  }

  private void prepareLocation() throws IOException {
    try {
      conductor.lock().lock();

      var data = Data.locate(getLocation(), instruction.id().name(), conductor.instructionFormat());
      data.set(instruction);
      data.save();
    } finally {
      conductor.lock().unlock();
    }
  }

  private void clearLocation() throws IOException {
    try {
      conductor.lock().lock();

      getLocation().resources().flatMap(r -> {
        try {
          r.delete();
          return Stream.empty();
        } catch (Exception e) {
          return Stream.of(e);
        }
      }).reduce((e1, e2) -> {
        e1.addSuppressed(e2);
        return e1;
      }).orElse(null);
    } finally {
      conductor.lock().unlock();
    }
  }
}
