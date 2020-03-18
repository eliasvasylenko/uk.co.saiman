package uk.co.saiman.experiment.conductor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.InstructionPlanningContext;
import uk.co.saiman.experiment.procedure.Procedures;

class OutgoingConditions {
  private final Lock lock;
  private final ExperimentPath<Absolute> path;
  private final Map<Class<?>, OutgoingCondition<?>> conditionPreparations;

  public OutgoingConditions(Lock lock, ExperimentPath<Absolute> path) {
    this.lock = lock;
    this.path = path;
    this.conditionPreparations = new HashMap<>();
  }

  public void addOutgoingCondition(Class<?> type, Evaluation evaluation) {}

  public <T> Optional<OutgoingCondition<T>> getOutgoingCondition(Class<T> type) {
    @SuppressWarnings("unchecked")
    var preparation = (OutgoingCondition<T>) conditionPreparations.get(type);
    return Optional.ofNullable(preparation);
  }

  Lock lock() {
    return lock;
  }

  ExperimentPath<Absolute> path() {
    return path;
  }

  public void update(Instruction instruction, LocalEnvironment environment) {
    Procedures
        .plan(
            instruction,
            environment.getGlobalEnvironment(),
            variables -> new InstructionPlanningContext() {
              @Override
              public void preparesCondition(Class<?> type, Evaluation evaluation) {
                conditionPreparations
                    .put(type, new OutgoingCondition<>(OutgoingConditions.this, type, evaluation));
              }
            });

  }

  public void invalidate() {
    conditionPreparations.values().forEach(OutgoingCondition::invalidate);
  }

  public void terminate() {
    conditionPreparations.values().forEach(OutgoingCondition::terminate);
  }
}
