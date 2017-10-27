package uk.co.saiman.experiment;

public interface WorkspaceEvent {
  ExperimentNode<?, ?> experimentNode();
  
  EventType eventType();
}
