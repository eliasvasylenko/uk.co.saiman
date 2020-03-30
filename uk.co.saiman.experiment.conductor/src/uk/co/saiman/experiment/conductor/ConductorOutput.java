package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Map;
import java.util.Optional;
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
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class ConductorOutput implements Output {
  private final Conductor conductor;
  private final Procedure procedure;
  private final LocalEnvironment environment;
  private final Data<Procedure> data;

  private final Map<WorkspaceExperimentPath, ExecutionManager> progress;
  private final HotObservable<OutputEvent> events = new HotObservable<>();

  private Output successor;

  ConductorOutput(Conductor conductor) {
    this.conductor = conductor;
    this.procedure = null;
    this.progress = Map.of();
  }

  private ConductorOutput(
      ConductorOutput previous,
      Procedure procedure,
      LocalEnvironment environment,
      Data<Procedure> data) {
    this.conductor = previous.conductor;
    this.procedure = procedure;
    this.environment = environment;
    this.data = data;

    try {
      conductor.lock().lock();

      data.set(procedure);
      data.save();

      procedure
          .instructionPaths()
          .forEach(
              path -> this.progress
                  .compute(
                      path,
                      (p, execution) -> Optional
                          .ofNullable(execution)
                          .orElseGet(() -> new ExecutionManager(this, p)))
                  .updateInstruction(procedure.instruction(path).get(), environment));

      procedure.instructionPaths().forEach(path -> this.progress.get(path).updateDependencies());

      this.progress.replaceAll((path, execution) -> execution.execute() ? execution : null);

      /*
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * TODO we must close the environment when the experiment is complete!!!!
       * 
       * 
       * TODO we also need to emit a completed event at that point
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       */

      // is there any point in this? it will always just immediately follow an
      // outputsucceeded event on the previous output...
      // nextOutput.nextEvent(new OutputBeginEvent(output));
    } catch (Exception e) {
      environment.close();
      throw e;

    } finally {
      conductor.lock().unlock();
    }
  }

  private void succeeded(Procedure successor) {
    if (data != null) {
      data.unset();
      data.save();
    }

    progress.values().stream().forEach(execution -> {
      execution.markRemoved();
      execution.execute();
    });

    events.next(new OutputSucceededEvent(this, successor));
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Output> successiveOutput() {
    // TODO Auto-generated method stub
    return null;
  }

  public void nextEvent(OutputEvent outputEvent) {
    // TODO Auto-generated method stub

  }

  Optional<ExecutionManager> findInstruction(WorkspaceExperimentPath path) {
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

      /*
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * We've tried putting openEnvironment here so that if it fails we don't have to
       * terminate the existing conductor output. But it's a bit awkward since we also
       * want to be careful about the ownership of the environment and closing it when
       * it's not needed any more ... and that's hard to do when we're passing it in
       * to its owner.
       * 
       * 
       * 
       * TODO Probably a better idea to just skip all this complexity and only try
       * opening the environment once we've already succeeded this conductor output.
       * 
       * 
       * 
       * 
       * 
       * 
       */
      var environment = Procedures
          .openEnvironment(procedure, conductor.environmentService(), 2, SECONDS);
      try {
        return succeed(new ConductorOutput(this, procedure, environment, data));
      } catch (Exception e) {
        try {
          environment.close();
        } catch (Exception e2) {
          e.addSuppressed(e2);
        }
        throw e;
      }
    } catch (Exception e) {
      throw new ConductorException(format("Unable to conduct procedure %s", procedure), e);
    }
  }

  public ConductorOutput succeedWithClear() {
    try {
      return succeed(new ConductorOutput(this, null, null, null));
    } catch (Exception e) {
      throw new ConductorException(format("Unable to terminate conductor"), e);
    }
  }

  private ConductorOutput succeed(ConductorOutput successor) throws Exception {
    try {
      conductor.lock().lock();

      if (data != null) {
        data.unset();
        data.save();
      }

      progress
          .values()
          .stream()
          .filter(
              execution -> successor
                  .procedure()
                  .flatMap(p -> p.instruction(execution.getPath()))
                  .isEmpty())
          .forEach(execution -> {
            execution.markRemoved();
            execution.execute();
          });

      this.successor = successor;
      events.next(new OutputSucceededEvent(this, successor));

      return successor;

    } finally {
      conductor.lock().unlock();
    }
  }
}
