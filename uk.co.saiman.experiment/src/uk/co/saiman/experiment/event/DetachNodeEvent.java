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

import static uk.co.saiman.experiment.event.ExperimentEventKind.DETACH;

import java.util.Optional;

import uk.co.saiman.experiment.ExperimentNode;

public class DetachNodeEvent extends ExperimentEvent {
  private final Optional<ExperimentNode<?, ?>> previousParent;

  public DetachNodeEvent(ExperimentNode<?, ?> node, ExperimentNode<?, ?> previousParent) {
    super(node);
    this.previousParent = Optional.ofNullable(previousParent);
  }

  public Optional<ExperimentNode<?, ?>> previousParent() {
    return previousParent;
  }

  @Override
  public ExperimentEventKind kind() {
    return DETACH;
  }
}