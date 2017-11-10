/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
