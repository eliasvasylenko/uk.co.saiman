package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.ATTACH;

import java.util.Optional;

import uk.co.saiman.experiment.ExperimentNode;

public class AttachNodeEvent extends ExperimentEvent {
  private final Optional<ExperimentNode<?, ?>> parent;

  public AttachNodeEvent(ExperimentNode<?, ?> node, ExperimentNode<?, ?> parent) {
    super(node);
    this.parent = Optional.ofNullable(parent);
  }

  public Optional<ExperimentNode<?, ?>> parent() {
    return parent;
  }

  @Override
  public ExperimentEventKind kind() {
    return ATTACH;
  }
}