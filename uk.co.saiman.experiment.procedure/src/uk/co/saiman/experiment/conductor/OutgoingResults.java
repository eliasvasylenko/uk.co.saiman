package uk.co.saiman.experiment.conductor;

import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

import uk.co.saiman.experiment.conductor.OutgoingResults.ResultObservation.IncomingResult;
import uk.co.saiman.experiment.dependency.Result;

public class OutgoingResults {
  public static class ResultObservation<T> {
    public static class IncomingResult<T> {
      private final Class<T> type;

      public IncomingResult(Class<T> type) {
        this.type = type;
      }

      public Result<T> acquire() {
        // TODO Auto-generated method stub
        return null;
      }

      public Class<T> type() {
        return type;
      }

      public void done() {
        // TODO Auto-generated method stub

      }
    }
  }

  private final Lock lock;

  public OutgoingResults(Lock lock) {
    this.lock = lock;
  }

  public Stream<InstructionExecution> consumers() {
    // TODO Auto-generated method stub
    return null;
  }

  public void invalidate() {
    // TODO Auto-generated method stub

  }

  public void add(Class<?> type) {
    // TODO Auto-generated method stub

  }

  public <T> IncomingResult<T> addConsumer(Class<T> type) {
    return null;
    // TODO Auto-generated method stub
  }

  public void terminate() {
    // TODO Auto-generated method stub

  }

  public <T> Result<T> acquire(Class<T> source, InstructionExecution consumer) {
    // TODO Auto-generated method stub
    return null;
  }
}
