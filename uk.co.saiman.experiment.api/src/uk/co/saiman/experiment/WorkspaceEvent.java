package uk.co.saiman.experiment;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Optional;

public interface WorkspaceEvent {
  /**
   * The type of workspace event.
   * 
   * @author Elias N Vasylenko
   */
  public enum EventType {
    /**
     * An experiment node has just been added.
     */
    ADD {
      public Optional<AddEvent> as(WorkspaceEvent event) {
        return event.eventType() == this ? of((AddEvent) event) : empty();
      }
    },

    /**
     * An experiment node has just been removed.
     */
    REMOVE {
      public Optional<RemoveEvent> as(WorkspaceEvent event) {
        return event.eventType() == this ? of((RemoveEvent) event) : empty();
      }
    },

    /**
     * An experiment node has just been renamed.
     */
    RENAME {
      public Optional<RenameEvent> as(WorkspaceEvent event) {
        return event.eventType() == this ? of((RenameEvent) event) : empty();
      }
    },

    /**
     * An experiment node is about to begin processing.
     * <p>
     * This event is fired whenever a node is about to move out of the
     * {@link ExperimentLifecycleState#CONFIGURATION configuration state}. At this
     * point an observer may attach to the experiment's lifecycle observable to
     * monitor further progress.
     */
    PROCESS {
      public Optional<ProcessEvent> as(WorkspaceEvent event) {
        return event.eventType() == this ? of((ProcessEvent) event) : empty();
      }
    };

    public abstract Optional<? extends WorkspaceEvent> as(WorkspaceEvent event);
  }

  interface AddEvent extends WorkspaceEvent {}

  interface RemoveEvent extends WorkspaceEvent {
    ExperimentNode<?, ?> previousParent();
  }

  interface RenameEvent extends WorkspaceEvent {
    String previousId();
  }

  interface ProcessEvent extends WorkspaceEvent {
    Experiment experimentNode();
  }

  ExperimentNode<?, ?> experimentNode();

  EventType eventType();
}
