package uk.co.saiman.experiment.event;

import uk.co.saiman.experiment.ExperimentNode;

public class RenameNodeEvent extends ExperimentEvent {
  private final String id;
  private final String previousId;

  public RenameNodeEvent(ExperimentNode<?, ?> node, String id) {
    super(node);
    this.id = id;
    this.previousId = node.getId();
  }

  public String id() {
    return id;
  }

  public String previousId() {
    return previousId;
  }

  @Override
  public ExperimentEventKind kind() {
    return ExperimentEventKind.RENAME;
  }
}