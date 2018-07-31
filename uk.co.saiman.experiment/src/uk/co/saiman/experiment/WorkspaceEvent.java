package uk.co.saiman.experiment;

import static java.util.Objects.requireNonNull;

public class WorkspaceEvent {
  private ExperimentNode<?, ?> node;
  private WorkspaceEventKind kind;
  private Runnable cancel;

  protected WorkspaceEvent(ExperimentNode<?, ?> node, WorkspaceEventKind kind, Runnable cancel) {
    this.node = requireNonNull(node);
    this.kind = requireNonNull(kind);
    this.cancel = cancel;
  }

  public static WorkspaceEvent workspaceEvent(ExperimentNode<?, ?> node, WorkspaceEventKind kind) {
    return new WorkspaceEvent(node, kind, null);
  }

  public static WorkspaceEvent workspaceEvent(
      ExperimentNode<?, ?> node,
      WorkspaceEventKind kind,
      Runnable cancel) {
    return new WorkspaceEvent(node, kind, requireNonNull(cancel));
  }

  public ExperimentNode<?, ?> getNode() {
    return node;
  }

  public WorkspaceEventKind getKind() {
    return kind;
  }

  /**
   * An event is considered cancellable if invocation of the {@link #cancel()}
   * method is <em>expected</em> to succeed. It does not provide a
   * <em>guarantee</em> that it will succeed.
   * <p>
   * Stale workspace events may change from being cancellable to not being
   * cancellable if the event is not intercepted and cancelled directly in the
   * firing thread. Some workspace events may continue to be cancellable after the
   * fact, which may facilitate e.g. an undo feature.
   * 
   * @return true if the event may be cancelled, false otherwise
   */
  public boolean isCancellable() {
    return cancel != null;
  }

  public synchronized void cancel() {
    if (cancel != null) {
      cancel.run();
      cancel = null;
    }
  }
}
