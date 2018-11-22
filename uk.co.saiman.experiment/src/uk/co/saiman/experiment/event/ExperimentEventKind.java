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
package uk.co.saiman.experiment.event;

import uk.co.saiman.experiment.ExperimentNode;

public enum ExperimentEventKind {
  /**
   * An experiment node was added to the workspace.
   */
  ATTACH(AttachNodeEvent.class),

  /**
   * An experiment node was removed from the workspace.
   */
  DETACH(DetachNodeEvent.class),

  /**
   * An experiment node's {@link ExperimentNode#getIndex() index} was updated.
   */
  REORDER(ReorderExperimentEvent.class),

  /**
   * An experiment node's {@link ExperimentNode#getId() id} was updated.
   */
  RENAME(RenameNodeEvent.class),

  /**
   * An experiment node's {@link ExperimentNode#getVariables() variables} were
   * updated.
   */
  STATE(ExperimentVariablesEvent.class),

  /**
   * An experiment node's {@link ExperimentNode#getLifecycleState() lifecycle
   * state} was updated.
   */
  LIFECYLE(ExperimentLifecycleEvent.class);

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
