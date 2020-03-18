package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;

import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.ConditionClosedException;
import uk.co.saiman.experiment.dependency.ConditionPath;

class IncomingCondition<T> {
  enum DependencyState {
    WAITING, ACQUIRED, DONE
  }

  private final OutgoingCondition<T> outgoing;
  private final java.util.concurrent.locks.Condition lockCondition;
  DependencyState state;

  public IncomingCondition(
      OutgoingCondition<T> outgoing,
      java.util.concurrent.locks.Condition lockCondition) {
    this.outgoing = outgoing;
    this.lockCondition = lockCondition;
    this.state = DependencyState.WAITING;
  }

  public Condition<T> acquire() {
    try {
      while (!outgoing.beginAcquire(this)) {
        if (state == DependencyState.DONE) {
          throw new ConductorException(
              format("Failed to prepare dependency to condition %s", outgoingPath()));
        }
        lockCondition.await();
      }
      state = DependencyState.ACQUIRED;
    } catch (InterruptedException e) {
      throw new ConductorException(
          format("Failed to acquire dependency to condition %s", outgoingPath()),
          e);
    }
    return new Condition<T>() {
      @SuppressWarnings("unchecked")
      @Override
      public Class<T> type() {
        return (Class<T>) path().getProduction();
      }

      @Override
      public ConditionPath<Absolute, T> path() {
        return path();
      }

      @Override
      public T value() {
        outgoing.lock().lock();
        try {
          if (getState() == DependencyState.DONE) {
            throw new ConditionClosedException(type());
          }
          return outgoing.resource();
        } finally {
          outgoing.lock().unlock();
        }
      }

      @Override
      public void close() {
        outgoing.lock().lock();
        try {
          done();
        } finally {
          outgoing.lock().unlock();
        }
      }
    };
  }

  void invalidate() {
    state = INVALID;
  }

  ConditionPath<Absolute, T> outgoingPath() {
    return outgoing.path();
  }

  public void done() {
    this.state = DependencyState.DONE;
    this.lockCondition.signalAll();
  }

  public DependencyState getState() {
    return this.state;
  }
}