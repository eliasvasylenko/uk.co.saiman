package uk.co.saiman.experiment.conductor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

import uk.co.saiman.experiment.conductor.IncomingCondition.DependencyState;
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
    boolean acquire;
    switch (evaluation) {
    case ORDERED:
      acquire = acquiredConsumers.stream().allMatch(c -> c.getState() == DependencyState.DONE)
          && nextConsumer() == conditionDependency;
    case UNORDERED:
      acquire = acquiredConsumers.stream().allMatch(c -> c.getState() == DependencyState.DONE);
    case PARALLEL:
    case INDEPENDENT:
    default:
      acquire = true;
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
      while (consumers.stream().anyMatch(c -> c.state != DependencyState.DONE)) {
        lockCondition.signalAll();
        lockCondition.await();
      }
    } catch (InterruptedException e) {
      throw new ConductorException("Cancelled preparation", e);
    } finally {
      resource = null;
    }
  }

  public IncomingCondition<T> addConsumer(ExperimentPath<Absolute> path) {
    var dependency = new IncomingCondition<>(this, lockCondition);
    consumers.add(dependency);
    return dependency;
  }

  public void invalidate() {
    consumers.forEach(IncomingCondition::invalidate);
    consumers.clear();
  }

  public void terminate() {
    consumers.forEach(IncomingCondition::done);
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