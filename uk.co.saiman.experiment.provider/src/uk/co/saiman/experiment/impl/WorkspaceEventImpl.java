package uk.co.saiman.experiment.impl;

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.experiment.WorkspaceEventState.CANCELLED;
import static uk.co.saiman.experiment.WorkspaceEventState.COMPLETED;
import static uk.co.saiman.experiment.WorkspaceEventState.PENDING;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.WorkspaceEvent;
import uk.co.saiman.experiment.WorkspaceEventKind;
import uk.co.saiman.experiment.WorkspaceEventState;

public class WorkspaceEventImpl implements WorkspaceEvent {
  private final ExperimentNode<?, ?> node;
  private final WorkspaceEventKind kind;
  private WorkspaceEventState state;

  public WorkspaceEventImpl(ExperimentNode<?, ?> node, WorkspaceEventKind kind) {
    this.node = requireNonNull(node);
    this.kind = requireNonNull(kind);
    this.state = PENDING;
  }

  @Override
  public ExperimentNode<?, ?> getNode() {
    return node;
  }

  @Override
  public WorkspaceEventKind getKind() {
    return kind;
  }

  @Override
  public WorkspaceEventState getState() {
    return state;
  }

  @Override
  public synchronized void cancel() {
    if (state == COMPLETED) {
      throw new IllegalStateException();
    }
    state = CANCELLED;
  }

  synchronized boolean complete() {
    if (state == CANCELLED) {
      return false;
    }
    state = COMPLETED;
    return true;
  }
}
