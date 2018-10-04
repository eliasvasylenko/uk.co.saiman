/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import java.util.Optional;

import uk.co.saiman.experiment.state.StateMap;

/**
 * An {@link Workspace#events(WorkspaceEventState) event} in an experiment
 * workspace. Workspace events are posted after the associated change to the
 * workspace is effected, such that listeners can easily respond to the change.
 * 
 * @author Elias N Vasylenko
 */
public interface WorkspaceEvent {
  ExperimentNode<?, ?> node();

  WorkspaceEventKind kind();

  WorkspaceEventState state();

  void cancel();

  @SuppressWarnings("unchecked")
  default <T extends WorkspaceEvent> Optional<T> as(Class<T> type) {
    if (type.isInstance(this)) {
      return Optional.of((T) this);
    } else {
      return Optional.empty();
    }
  }

  interface AddExperimentEvent extends WorkspaceEvent {
    /**
     * @return an optional containing the parent to add to, or an empty optional if
     *         it's a root experiment
     */
    Optional<ExperimentNode<?, ?>> parent();

    @Override
    default WorkspaceEventKind kind() {
      return WorkspaceEventKind.ADD;
    }
  }

  interface RemoveExperimentEvent extends WorkspaceEvent {
    /**
     * @return an optional containing the parent to remove from, or an empty
     *         optional if it's a root experiment
     */
    Optional<ExperimentNode<?, ?>> previousParent();

    @Override
    default WorkspaceEventKind kind() {
      return WorkspaceEventKind.REMOVE;
    }
  }

  interface MoveExperimentEvent extends WorkspaceEvent {
    ExperimentNode<?, ?> parent();

    ExperimentNode<?, ?> previousParent();

    @Override
    default WorkspaceEventKind kind() {
      return WorkspaceEventKind.MOVE;
    }
  }

  interface RenameExperimentEvent extends WorkspaceEvent {
    String id();

    String previousId();

    @Override
    default WorkspaceEventKind kind() {
      return WorkspaceEventKind.RENAME;
    }
  }

  interface ExperimentStateEvent extends WorkspaceEvent {
    StateMap stateMap();

    StateMap previousStateMap();

    @Override
    default WorkspaceEventKind kind() {
      return WorkspaceEventKind.STATE;
    }
  }

  interface ExperimentLifecycleEvent extends WorkspaceEvent {
    ExperimentLifecycleState lifecycleState();

    ExperimentLifecycleState previousLifecycleState();

    @Override
    default WorkspaceEventKind kind() {
      return WorkspaceEventKind.LIFECYLE;
    }
  }

  interface ExperimentTypeEvent extends WorkspaceEvent {
    ExperimentType<?, ?> type();

    ExperimentType<?, ?> previousType();

    @Override
    default WorkspaceEventKind kind() {
      return WorkspaceEventKind.TYPE;
    }
  }
}
