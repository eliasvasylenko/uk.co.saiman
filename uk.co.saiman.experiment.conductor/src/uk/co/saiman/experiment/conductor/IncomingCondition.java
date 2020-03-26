package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;

import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.ConditionClosedException;
import uk.co.saiman.experiment.dependency.ConditionPath;

class IncomingCondition<T> {
  private final OutgoingCondition<T> outgoing;
  private final java.util.concurrent.locks.Condition lockCondition;
  IncomingDependencyState state;

  public IncomingCondition(
      OutgoingCondition<T> outgoing,
      java.util.concurrent.locks.Condition lockCondition) {
    this.outgoing = outgoing;
    this.lockCondition = lockCondition;
    this.state = IncomingDependencyState.WAITING;
  }

  public Condition<T> acquire() {
    try {
      while (!outgoing.beginAcquire(this)) {
        if (state == IncomingDependencyState.DONE) {
          throw new ConductorException(
              format(
                  "Failed to prepare dependency to condition %s at %s",
                  outgoing.type(),
                  outgoing.path()));
        }
        lockCondition.await();
      }
      state = IncomingDependencyState.ACQUIRED;
    } catch (InterruptedException e) {
      throw new ConductorException(
          format(
              "Failed to acquire dependency to condition %s at %s",
              outgoing.type(),
              outgoing.path()),
          e);
    }
    return new Condition<T>() {
      @SuppressWarnings("unchecked")
      @Override
      public Class<T> type() {
        return (Class<T>) path().getProduction();
      }

      @Override
      public ConditionStatus status() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public ConditionPath<Absolute, T> path() {
        return path();
      }

      @Override
      public T value() {
        outgoing.lock().lock();
        try {
          if (getState() == IncomingDependencyState.DONE) {
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

  void invalidateIncoming() {
    state = IncomingDependencyState.WAITING;
    outgoing.invalidatedIncoming(this);
  }

  void invalidatedOutgoing() {
    state = IncomingDependencyState.WAITING;
  }

  public void done() {
    this.state = IncomingDependencyState.DONE;
    this.lockCondition.signalAll();
  }

  public IncomingDependencyState getState() {
    return this.state;
  }

  public Class<T> type() {
    return outgoing.type();
  }
}