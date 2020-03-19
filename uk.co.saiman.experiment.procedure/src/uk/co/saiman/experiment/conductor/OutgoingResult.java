package uk.co.saiman.experiment.conductor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.ResultPath;

class OutgoingResult<T> {
  private final OutgoingResults results;
  private final Class<T> type;

  private final java.util.concurrent.locks.Condition lockCondition;

  private final HashMap<ExperimentPath<Absolute>, IncomingResult<T>> consumers = new LinkedHashMap<>();
  private final List<IncomingResult<T>> acquiredResults = new ArrayList<>();
  private T resource;

  public OutgoingResult(OutgoingResults results, Class<T> type) {
    this.results = results;
    this.type = type;

    this.lockCondition = results.lock().newCondition();
  }

  public boolean beginAcquire(IncomingResult<T> resultDependency) {
    if (resource == null) {
      return false;
    }
    this.acquiredResults.add(resultDependency);
    return true;
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

  public IncomingResult<T> addConsumer(ExperimentPath<Absolute> path) {
    System.out.println("   adding consumer! " + path);
    return consumers.computeIfAbsent(path, p -> new IncomingResult<>(this, lockCondition));
  }

  public void invalidate() {
    consumers.values().forEach(IncomingResult::invalidatedOutgoing);
    consumers.clear();
    acquiredResults.clear();
  }

  public void terminate() {
    consumers.values().forEach(IncomingResult::done);
  }

  Lock lock() {
    return results.lock();
  }

  ResultPath<Absolute, T> path() {
    return ProductPath.toResult(results.path(), type);
  }

  T resource() {
    return resource;
  }
}