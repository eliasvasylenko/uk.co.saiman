package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.output.event.OutputEvent;
import uk.co.saiman.experiment.output.event.OutputSucceededEvent;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.procedure.json.JsonProcedureFormat;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class ConductorOutput implements Output {
  private final Conductor conductor;
  private final Procedure procedure;
  private final Data<Procedure> data;
  private final Map<WorkspaceExperimentPath, ExecutionManager> progress;

  private LocalEnvironment environment;

  private final HotObservable<OutputEvent> events = new HotObservable<>();

  private ConductorOutput successor;

  ConductorOutput(Conductor conductor) {
    this.conductor = conductor;
    this.procedure = null;
    this.data = null;
    this.progress = Map.of();
  }

  private ConductorOutput(
      Conductor conductor,
      Procedure procedure,
      Data<Procedure> data,
      Map<WorkspaceExperimentPath, ExecutionManager> progress) {
    this.conductor = conductor;
    this.procedure = procedure;
    this.data = data;
    this.progress = progress;
  }

  private void start() throws Exception {
    environment = Procedures.openEnvironment(procedure, conductor.environmentService(), 2, SECONDS);
    try {
      data.set(procedure);
      data.save();

      System.out.println(" PROC: " + procedure);
      System.out.println(" PROG: " + progress);

      procedure
          .instructionPaths()
          .forEach(i -> progress.get(i).updateInstruction(procedure, environment));
      procedure.instructionPaths().forEach(i -> progress.get(i).updateDependencies(this));
      procedure.instructionPaths().forEach(i -> progress.get(i).execute());

      // is there any point in this? it will always just immediately follow an
      // outputsucceeded event on the previous output...
      // nextOutput.nextEvent(new OutputBeginEvent(output));

    } finally {
      // TODO attach environment close and end event to the join of any executor
      // threads.
      conductor.getExecutor().execute(() -> {
        try {
          progress.values().forEach(e -> {
            try {
              e.join();
            } catch (Exception x) {}
          });

        } finally {
          try {
            environment.close();
          } catch (Exception e) {
            throw new ConductorException(
                format("Failed to complete procedure normally %s", procedure),
                e);
          }
        }
      });
    }
  }

  @Override
  public Stream<Result<?>> results() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <U extends ExperimentPath<U>> Stream<ProductPath<U, ? extends Result<?>>> resultPaths(
      ExperimentPath<U> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> T resolveResult(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Observable<OutputEvent> events() {
    return events;
  }

  @Override
  public Optional<Output> successiveOutput() {
    return Optional.ofNullable(successor);
  }

  Optional<ExecutionManager> findInstruction(WorkspaceExperimentPath path) {
    System.out.println("   find " + path);
    System.out.println(progress);
    System.out.println(progress.get(path));
    return Optional.ofNullable(progress.get(path));
  }

  public Optional<Procedure> procedure() {
    return Optional.ofNullable(procedure);
  }

  public ConductorOutput succeedWithProcedure(Procedure procedure) {
    try {
      Procedures.validateDependencies(procedure);

      var storage = conductor.storageConfiguration().locateStorage(procedure.path());
      var procedureFormat = new JsonProcedureFormat(
          conductor.instructionFormat(),
          procedure.environment());
      var data = Data.locate(storage.location(), procedure.id().name(), procedureFormat);

      try {
        conductor.lock().lock();

        clearData();
        var progress = inheritOrTerminateProgress(procedure);

        successor = new ConductorOutput(conductor, procedure, data, progress);
        events.next(new OutputSucceededEvent(this));

        successor.start();

        return successor;

      } finally {
        conductor.lock().unlock();
      }
    } catch (Exception e) {
      var ce = new ConductorException(format("Unable to conduct procedure %s", procedure), e);
      conductor.log().log(Level.ERROR, ce);
      throw ce;
    }
  }

  public ConductorOutput succeedWithClear() {
    try {
      try {
        conductor.lock().lock();

        clearData();
        terminateProgress();

        successor = new ConductorOutput(conductor);
        events.next(new OutputSucceededEvent(this));

        return successor;

      } finally {
        conductor.lock().unlock();
      }
    } catch (Exception e) {
      var ce = new ConductorException(format("Unable to terminate conductor"), e);
      conductor.log().log(Level.ERROR, ce);
      throw ce;
    }
  }

  private void clearData() {
    if (data != null) {
      data.unset();
      data.save();
    }
  }

  private Map<WorkspaceExperimentPath, ExecutionManager> inheritOrTerminateProgress(
      Procedure procedure) {
    var thisProgress = new HashMap<>(this.progress);
    var progress = procedure
        .instructionPaths()
        .collect(
            toMap(
                Function.identity(),
                p -> Optional
                    .ofNullable(thisProgress.remove(p))
                    .orElseGet(() -> new ExecutionManager(conductor, p))));
    thisProgress.values().forEach(ExecutionManager::remove);
    return progress;
  }

  private void terminateProgress() {
    this.progress.values().forEach(ExecutionManager::remove);
  }

  Conductor getConductor() {
    return conductor;
  }

  LocalEnvironment environment() {
    return environment;
  }
}
