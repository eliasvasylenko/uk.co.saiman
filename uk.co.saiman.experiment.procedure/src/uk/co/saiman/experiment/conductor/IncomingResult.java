package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;

import java.util.Optional;

import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.observable.Observable;

class IncomingResult<T> {
  private final OutgoingResult<T> outgoing;
  private final java.util.concurrent.locks.Condition lockCondition;
  IncomingDependencyState state;

  public IncomingResult(
      OutgoingResult<T> outgoing,
      java.util.concurrent.locks.Condition lockCondition) {
    this.outgoing = outgoing;
    this.lockCondition = lockCondition;
    this.state = IncomingDependencyState.WAITING;
  }

  public Result<T> acquire() {
    try {
      while (!outgoing.beginAcquire(this)) {
        if (state == IncomingDependencyState.DONE) {
          throw new ConductorException(
              format("Failed to prepare dependency to condition %s", outgoingPath()));
        }
        lockCondition.await();
      }
      state = IncomingDependencyState.ACQUIRED;
    } catch (InterruptedException e) {
      throw new ConductorException(
          format("Failed to acquire dependency to condition %s", outgoingPath()),
          e);
    }
    return new Result<T>() {
      @SuppressWarnings("unchecked")
      @Override
      public Class<T> type() {
        return (Class<T>) path().getProduction();
      }

      @Override
      public ResultPath<Absolute, T> path() {
        return path();
      }

      @Override
      public Observable<Result<T>> updates() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean isComplete() {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean isPartial() {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public Optional<T> value() {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  void invalidateIncoming() {
    state = IncomingDependencyState.WAITING;
  }

  void invalidatedOutgoing() {
    state = IncomingDependencyState.WAITING;
  }

  ResultPath<Absolute, T> outgoingPath() {
    return outgoing.path();
  }

  public void done() {
    this.state = IncomingDependencyState.DONE;
    this.lockCondition.signalAll();
  }

  public IncomingDependencyState getState() {
    return this.state;
  }
}