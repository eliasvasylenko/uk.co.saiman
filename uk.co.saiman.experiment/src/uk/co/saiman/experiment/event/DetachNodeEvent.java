package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.DETACH;

import java.util.Optional;

import uk.co.saiman.experiment.ExperimentNode;

public class DetachNodeEvent extends ExperimentEvent {
  private final Optional<ExperimentNode<?, ?>> previousParent;

  public DetachNodeEvent(ExperimentNode<?, ?> node, ExperimentNode<?, ?> previousParent) {
    super(node);
    this.previousParent = Optional.ofNullable(previousParent);
  }

  public Optional<ExperimentNode<?, ?>> previousParent() {
    return previousParent;
  }

  @Override
  public ExperimentEventKind kind() {
    return DETACH;
  }
}