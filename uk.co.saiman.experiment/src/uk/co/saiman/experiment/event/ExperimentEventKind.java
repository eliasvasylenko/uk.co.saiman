package uk.co.saiman.experiment.event;

public enum ExperimentEventKind {
  RENAME_EXPERIMENT(RenameExperimentEvent.class),

  ADD_STEP(AddStepEvent.class),

  REMOVE_STEP(RemoveStepEvent.class),

  MOVE_STEP(MoveStepEvent.class),

  CHANGE_VARIABLE(ChangeVariableEvent.class),

  EXPERIMENT_SCHEDULE(ExperimentSchedulingEvent.class);

  private final Class<? extends ExperimentEvent> type;

  private ExperimentEventKind(Class<? extends ExperimentEvent> type) {
    this.type = type;
  }

  public boolean matches(ExperimentEvent event) {
    return event.kind() == this;
  }

  public Class<? extends ExperimentEvent> type() {
    return type;
  }
}
