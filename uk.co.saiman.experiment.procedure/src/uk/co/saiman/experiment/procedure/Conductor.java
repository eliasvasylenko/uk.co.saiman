package uk.co.saiman.experiment.procedure;

import static java.lang.String.format;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.production.ProductPath;
import uk.co.saiman.experiment.production.Result;
import uk.co.saiman.experiment.production.Results;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.Observable;

public class Conductor implements Results {
  private final StorageConfiguration<?> storageConfiguration;

  private final Map<ExperimentPath<?>, ExecutorProgress<?>> progress;
  private Procedure procedure;

  public Conductor(StorageConfiguration<?> storageConfiguration) {
    this.storageConfiguration = storageConfiguration;
    this.progress = new HashMap<>();
  }

  public StorageConfiguration<?> storageConfiguration() {
    return storageConfiguration;
  }

  public void conduct(Procedure procedure) {
    this.procedure = procedure;

    var progressIterator = progress.entrySet().iterator();
    while (progressIterator.hasNext()) {
      var progress = progressIterator.next();

      procedure
          .instruction(progress.getKey())
          .ifPresentOrElse(progress.getValue()::updateInstruction, () -> {
            progress.getValue().interrupt();
            progressIterator.remove();
          });
    }
    procedure
        .instructions()
        .filter(instruction -> progress.containsKey(instruction.path()))
        .forEach(
            instruction -> progress
                .put(instruction.path(), new ExecutorProgress<>(this, instruction)));
  }

  public Optional<Procedure> procedure() {
    return Optional.of(procedure);
  }

  public synchronized void interrupt() {
    // TODO cancel anything ongoing...
  }

  public synchronized void clear() {
    interrupt();

    try {
      storageConfiguration.locateStorage(ExperimentPath.defineAbsolute()).deallocate();
    } catch (IOException e) {
      throw new ConductorException(format("Unable to clear conducted procedure %s", procedure), e);
    }

    procedure = null;

  }

  @Override
  public Stream<Result<?>> results() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> T resolveResult(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> Observable<T> results(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }
}
