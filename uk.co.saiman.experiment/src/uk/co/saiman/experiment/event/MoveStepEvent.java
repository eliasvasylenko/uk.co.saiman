package uk.co.saiman.experiment.event;

public class MoveStepEvent extends ExperimentStepEvent {
  @Override
  public ExperimentEventKind kind() {
    return ExperimentEventKind.MOVE_STEP;
  }

  public String id() {
    // TODO Auto-generated method stub
    return null;
  }
}
