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

import static uk.co.saiman.experiment.event.ExperimentEventKind.REORDER;

import uk.co.saiman.experiment.ExperimentStep;

public class ReorderStepsEvent extends ExperimentEvent {
  private final int newIndex;
  private final int previousIndex;

  public ReorderStepsEvent(ExperimentStep<?> experiment, int newIndex, int previousIndex) {
    super(experiment);
    this.newIndex = newIndex;
    this.previousIndex = previousIndex;
  }

  public int newIndex() {
    return newIndex;
  }

  public int previousIndex() {
    return previousIndex;
  }

  @Override
  public ExperimentEventKind kind() {
    return REORDER;
  }
}
