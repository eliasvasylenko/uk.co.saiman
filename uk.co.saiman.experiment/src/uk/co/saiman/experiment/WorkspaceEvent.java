package uk.co.saiman.experiment;

public interface WorkspaceEvent {
  ExperimentNode<?, ?> getNode();

  WorkspaceEventType getType();

  /**
   * An event is considered cancellable is the {@link #cancel()} method is
   * expected to succeed. It does not provide a guarantee that it will succeed.
   * <p>
   * Stale workspace events may change from being cancellable to not being
   * cancellable if the event is not intercepted and cancelled directly in the
   * firing thread. Some workspace events may continue to be cancellable after the
   * fact, which may facilitate e.g. an undo feature.
   * 
   * @return true if the event may be cancelled, false otherwise
   */
  boolean isCancellable();

  void cancel();
}
