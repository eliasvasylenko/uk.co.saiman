package uk.co.saiman.observable;

import java.util.concurrent.CancellationException;

public class Cancellation {
  boolean cancelled;
  boolean complete;

  public synchronized void cancel() {
    if (complete) {
      throw new AlreadyCompletedException();
    }
    cancelled = true;
  }

  public synchronized boolean complete() {
    complete = true;
    return !cancelled;
  }

  public synchronized void completeOrThrow() {
    complete = true;
    if (cancelled) {
      throw new CancellationException();
    }
  }
}
