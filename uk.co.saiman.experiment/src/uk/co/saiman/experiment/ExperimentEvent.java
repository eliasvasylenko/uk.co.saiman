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

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public abstract class ExperimentEvent {
  private final ExperimentNode<?, ?> node;

  public ExperimentEvent(ExperimentNode<?, ?> node) {
    this.node = requireNonNull(node);
  }

  public ExperimentNode<?, ?> node() {
    return node;
  }

  public abstract ExperimentEventKind kind();

  @Override
  public String toString() {
    return kind() + "(" + node().getId() + ")";
  }

  @SuppressWarnings("unchecked")
  public <T extends ExperimentEvent> Optional<T> as(Class<T> type) {
    if (type.isInstance(this)) {
      return Optional.of((T) this);
    } else {
      return Optional.empty();
    }
  }
}
