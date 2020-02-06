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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import static java.util.Objects.requireNonNull;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.ConditionClosedException;
import uk.co.saiman.experiment.dependency.ProductPath;

public class ConditionImpl<T> implements Condition<T> {
  private final Class<T> type;
  private final ProductPath<Absolute, Condition<T>> path;
  private T value;

  public ConditionImpl(Class<T> type, ExperimentPath<Absolute> experimentPath, T value) {
    this.type = requireNonNull(type);
    this.path = ProductPath.toCondition(experimentPath, type);
    this.value = requireNonNull(value);
  }

  @Override
  public Class<T> type() {
    return type;
  }

  @Override
  public ProductPath<Absolute, Condition<T>> path() {
    return path;
  }

  @Override
  public T value() {
    T value = this.value;
    if (value == null) {
      throw new ConditionClosedException(type);
    }
    return value;
  }

  @Override
  public void close() {
    value = null;
  }

  public boolean isOpen() {
    return value != null;
  }
}
