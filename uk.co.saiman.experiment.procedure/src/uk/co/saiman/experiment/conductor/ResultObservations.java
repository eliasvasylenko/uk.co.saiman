package uk.co.saiman.experiment.conductor;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.experiment.procedure.InstructionDependencies;

public class ResultObservations {
  private final Lock lock;
  private final Supplier<InstructionDependencies> dependencies;

  public ResultObservations(Lock lock, Supplier<InstructionDependencies> dependencies) {
    this.lock = lock;
    this.dependencies = dependencies;
  }

  public Stream<InstructionExecution> consumers() {
    // TODO Auto-generated method stub
    return null;
  }
}
