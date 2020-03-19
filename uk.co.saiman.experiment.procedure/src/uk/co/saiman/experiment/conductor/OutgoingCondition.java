package uk.co.saiman.experiment.conductor;

import static uk.co.saiman.experiment.executor.Evaluation.ORDERED;
import static uk.co.saiman.experiment.executor.Evaluation.PARALLEL_TOGETHER;
import static uk.co.saiman.experiment.executor.Evaluation.SERIAL_TOGETHER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ConditionPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.executor.Evaluation;

class OutgoingCondition<T> {
  private final OutgoingConditions conditions;
  private final Class<T> type;
  private final Evaluation evaluation;

  private final java.util.concurrent.locks.Condition lockCondition;

  private final HashMap<ExperimentPath<Absolute>, IncomingCondition<T>> consumers = new LinkedHashMap<>();
  private final List<IncomingCondition<T>> acquiredConsumers = new ArrayList<>();
  private T resource;

  public OutgoingCondition(OutgoingConditions conditions, Class<T> type, Evaluation evaluation) {
    this.conditions = conditions;
    this.type = type;
    this.evaluation = evaluation;

    this.lockCondition = conditions.lock().newCondition();
  }

  public boolean beginAcquire(IncomingCondition<T> conditionDependency) {
    if (resource == null) {
      return false;
    }
    // TODO switch statement in java 14
    boolean acquire;
    switch (evaluation) {
    case ORDERED:
      acquire = acquiredConsumers.stream().allMatch(c -> c.getState() == IncomingDependencyState.DONE)
          && nextConsumer() == conditionDependency;
      break;
    case SERIAL_TOGETHER:
    case SERIAL:
      acquire = acquiredConsumers.stream().allMatch(c -> c.getState() == IncomingDependencyState.DONE);
      break;
    default:
      acquire = true;
      break;
    }
    if (acquire) {
      this.acquiredConsumers.add(conditionDependency);
    }
    return acquire;
  }

  private IncomingCondition<T> nextConsumer() {
    return consumers
        .values()
        .stream()
        .filter(c -> !acquiredConsumers.contains(c))
        .findFirst()
        .orElse(null);
  }

  public void prepare(T resource) {
    this.resource = Objects.requireNonNull(resource);

    try {
      while (consumers.values().stream().anyMatch(c -> c.state != IncomingDependencyState.DONE)) {
        lockCondition.signalAll();
        lockCondition.await();
      }
    } catch (InterruptedException e) {
      throw new ConductorException("Cancelled preparation", e);
    } finally {
      System.out.println("  ####### DONE PREPARE! " + consumers);
      resource = null;
    }
  }

  public IncomingCondition<T> addConsumer(ExperimentPath<Absolute> path) {
    System.out.println("   adding consumer! " + path);
    return consumers.computeIfAbsent(path, p -> new IncomingCondition<>(this, lockCondition));
  }

  public void invalidate() {
    consumers.values().forEach(IncomingCondition::invalidatedOutgoing);
    consumers.clear();
    acquiredConsumers.clear();
  }

  public void invalidatedIncoming(IncomingCondition<T> incoming) {
    if (acquiredConsumers.contains(incoming) && (evaluation == ORDERED
        || evaluation == PARALLEL_TOGETHER || evaluation == SERIAL_TOGETHER)) {
      invalidate();
    }
  }

  public void terminate() {
    consumers.values().forEach(IncomingCondition::done);
  }

  Lock lock() {
    return conditions.lock();
  }

  ConditionPath<Absolute, T> path() {
    return ProductPath.toCondition(conditions.path(), type);
  }

  T resource() {
    return resource;
  }
}