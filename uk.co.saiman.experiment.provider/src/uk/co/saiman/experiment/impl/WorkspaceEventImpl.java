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

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.WorkspaceEvent;
import uk.co.saiman.experiment.WorkspaceEventKind;
import uk.co.saiman.experiment.WorkspaceEventState;

public class WorkspaceEventImpl implements WorkspaceEvent {
  private final ExperimentNode<?, ?> node;
  private final WorkspaceEventKind kind;
  private WorkspaceEventState state;

  public WorkspaceEventImpl(ExperimentNode<?, ?> node, WorkspaceEventKind kind) {
    this.node = requireNonNull(node);
    this.kind = requireNonNull(kind);
    this.state = PENDING;
  }

  @Override
  public ExperimentNode<?, ?> getNode() {
    return node;
  }

  @Override
  public WorkspaceEventKind getKind() {
    return kind;
  }

  @Override
  public WorkspaceEventState getState() {
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
}
