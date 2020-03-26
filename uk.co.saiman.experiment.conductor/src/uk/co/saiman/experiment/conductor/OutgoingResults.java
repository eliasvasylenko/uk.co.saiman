package uk.co.saiman.experiment.conductor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.InstructionPlanningContext;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

public class OutgoingResults {
  private final Lock lock;
  private final WorkspaceExperimentPath path;
  private final Map<Class<?>, OutgoingResult<?>> resultPreparations;

  public OutgoingResults(Lock lock, WorkspaceExperimentPath path) {
    this.lock = lock;
    this.path = path;
    this.resultPreparations = new HashMap<>();
  }

  public void addOutgoingResult(Class<?> type, Evaluation evaluation) {}

  public <T> Optional<OutgoingResult<T>> getOutgoingResult(Class<T> type) {
    @SuppressWarnings("unchecked")
    var preparation = (OutgoingResult<T>) resultPreparations.get(type);
    return Optional.ofNullable(preparation);
  }

  Lock lock() {
    return lock;
  }

  WorkspaceExperimentPath path() {
    return path;
  }

  public void update(Instruction instruction, LocalEnvironment environment) {
    Procedures
        .plan(
            instruction,
            environment.getGlobalEnvironment(),
            variables -> new InstructionPlanningContext() {
              @Override
              public void observesResult(Class<?> type) {
                resultPreparations.put(type, new OutgoingResult<>(OutgoingResults.this, type));
              }
            });

  }

  public void invalidate() {
    resultPreparations.values().forEach(OutgoingResult::invalidate);
  }

  public void terminate() {
    resultPreparations.values().forEach(OutgoingResult::terminate);
  }
}
