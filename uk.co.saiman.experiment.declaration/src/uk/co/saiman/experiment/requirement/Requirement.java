/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.graph.
 *
 * uk.co.saiman.experiment.graph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.graph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.requirement;

import java.util.Objects;

import uk.co.saiman.experiment.dependency.Dependency;

/**
 * A source is simply a representation of an API point. In particular, it
 * represents an artifact which can acquired during the execution of an
 * experiment procedure, and the Java type which the artifact may be
 * materialized as.
 */
public abstract class Requirement<T, U extends Dependency<T>> {
  private final Class<?> type;

  // TODO sealed interface when language feature becomes available
  Requirement(Class<?> type) {
    this.type = type;
  }

  public Class<?> type() {
    return type;
  }

  public static <T> Observation<T> onResult(Class<T> type) {
    return new Observation<>(type);
  }

  public static <T> Preparation<T> onCondition(Class<T> type) {
    return new Preparation<>(type);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (Requirement<?, ?>) obj;

    return Objects.equals(this.type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
