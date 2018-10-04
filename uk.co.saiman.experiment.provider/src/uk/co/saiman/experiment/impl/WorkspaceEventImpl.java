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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.experiment.WorkspaceEventState.CANCELLED;
import static uk.co.saiman.experiment.WorkspaceEventState.COMPLETED;
import static uk.co.saiman.experiment.WorkspaceEventState.PENDING;

import java.util.Optional;

import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.WorkspaceEvent;
import uk.co.saiman.experiment.WorkspaceEventState;
import uk.co.saiman.experiment.state.StateMap;

public abstract class WorkspaceEventImpl implements WorkspaceEvent {
  private final ExperimentNode<?, ?> node;
  private WorkspaceEventState state;

  public WorkspaceEventImpl(ExperimentNode<?, ?> node) {
    this.node = requireNonNull(node);
    this.state = PENDING;
  }

  @Override
  public ExperimentNode<?, ?> node() {
    return node;
  }

  @Override
  public WorkspaceEventState state() {
    return state;
  }

  @Override
  public synchronized void cancel() {
    if (state == COMPLETED) {
      throw new IllegalStateException();
    }
    state = CANCELLED;
  }

  synchronized boolean complete() {
    if (state == CANCELLED) {
      return false;
    }
    state = COMPLETED;
    return true;
  }

  @Override
  public String toString() {
    return kind().type().getSimpleName() + "(" + node.getId() + " " + kind() + " " + state + ")";
  }

  public static class AddExperimentEventImpl extends WorkspaceEventImpl
      implements AddExperimentEvent {
    private final Optional<ExperimentNode<?, ?>> parent;

    public AddExperimentEventImpl(ExperimentNode<?, ?> node, ExperimentNode<?, ?> parent) {
      super(node);
      this.parent = Optional.ofNullable(parent);
    }

    @Override
    public Optional<ExperimentNode<?, ?>> parent() {
      return parent;
    }
  }

  public static class RemoveExperimentEventImpl extends WorkspaceEventImpl
      implements RemoveExperimentEvent {
    private final Optional<ExperimentNode<?, ?>> previousParent;

    public RemoveExperimentEventImpl(
        ExperimentNode<?, ?> node,
        ExperimentNode<?, ?> previousParent) {
      super(node);
      this.previousParent = Optional.ofNullable(previousParent);
    }

    @Override
    public Optional<ExperimentNode<?, ?>> previousParent() {
      return previousParent;
    }
  }

  public static class MoveExperimentEventImpl extends WorkspaceEventImpl
      implements MoveExperimentEvent {
    private final ExperimentNode<?, ?> parent;
    private final ExperimentNode<?, ?> previousParent;

    public MoveExperimentEventImpl(
        ExperimentNode<?, ?> node,
        ExperimentNode<?, ?> parent,
        ExperimentNode<?, ?> previousParent) {
      super(node);
      this.parent = parent;
      this.previousParent = previousParent;
    }

    @Override
    public ExperimentNode<?, ?> parent() {
      return parent;
    }

    @Override
    public ExperimentNode<?, ?> previousParent() {
      return previousParent;
    }
  }

  public static class RenameExperimentEventImpl extends WorkspaceEventImpl
      implements RenameExperimentEvent {
    private final String id;
    private final String previousId;

    public RenameExperimentEventImpl(ExperimentNode<?, ?> node, String id, String previousId) {
      super(node);
      this.id = id;
      this.previousId = previousId;
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String previousId() {
      return previousId;
    }
  }

  public static class ExperimentStateEventImpl extends WorkspaceEventImpl
      implements ExperimentStateEvent {
    private final StateMap stateMap;
    private final StateMap previousStateMap;

    public ExperimentStateEventImpl(
        ExperimentNode<?, ?> node,
        StateMap stateMap,
        StateMap previousStateMap) {
      super(node);
      this.stateMap = stateMap;
      this.previousStateMap = previousStateMap;
    }

    @Override
    public StateMap stateMap() {
      return stateMap;
    }

    @Override
    public StateMap previousStateMap() {
      return previousStateMap;
    }
  }

  public static class ExperimentLifecycleEventImpl extends WorkspaceEventImpl
      implements ExperimentLifecycleEvent {
    private final ExperimentLifecycleState lifecycleState;
    private final ExperimentLifecycleState previousLifecycleState;

    public ExperimentLifecycleEventImpl(
        ExperimentNode<?, ?> node,
        ExperimentLifecycleState lifecycleState,
        ExperimentLifecycleState previousLifecycleState) {
      super(node);
      this.lifecycleState = lifecycleState;
      this.previousLifecycleState = previousLifecycleState;
    }

    @Override
    public ExperimentLifecycleState lifecycleState() {
      return lifecycleState;
    }

    @Override
    public ExperimentLifecycleState previousLifecycleState() {
      return previousLifecycleState;
    }
  }

  public static class ExperimentTypeEventImpl extends WorkspaceEventImpl
      implements ExperimentTypeEvent {
    private final ExperimentType<?, ?> type;
    private final ExperimentType<?, ?> previousType;

    public ExperimentTypeEventImpl(
        ExperimentNode<?, ?> node,
        ExperimentType<?, ?> type,
        ExperimentType<?, ?> previousType) {
      super(node);
      this.type = type;
      this.previousType = previousType;
    }

    @Override
    public ExperimentType<?, ?> type() {
      return type;
    }

    @Override
    public ExperimentType<?, ?> previousType() {
      return previousType;
    }
  }
}
