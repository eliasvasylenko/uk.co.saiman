package uk.co.saiman.experiment.event;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.procedure.Procedure;

public abstract class ExperimentEvent {
  public abstract ExperimentEventKind kind();

  public Procedure procedure() {
    // TODO Auto-generated method stub
    return null;
  }

  public Experiment experiment() {
    // TODO Auto-generated method stub
    return null;
  }
}
