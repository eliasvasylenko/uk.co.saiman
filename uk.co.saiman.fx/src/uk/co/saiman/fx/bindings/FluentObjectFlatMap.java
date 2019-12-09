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
 * This file is part of uk.co.saiman.fx.
 *
 * uk.co.saiman.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.fx.bindings;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;

public class FluentObjectFlatMap<T, U> extends FluentObjectBinding<T> {
  private final ObservableValue<U> value;
  private final Function<U, ObservableValue<T>> mapping;

  private ObservableValue<T> mappedValue;

  public FluentObjectFlatMap(ObservableValue<U> value, Function<U, ObservableValue<T>> mapping) {
    this.value = requireNonNull(value);
    this.mapping = requireNonNull(mapping);
  }

  @Override
  protected synchronized T computeValue() {
    if (mappedValue != null) {
      unbind(value, mappedValue);
    } else {
      unbind(value);
    }

    U fromValue = value.getValue();

    mappedValue = fromValue == null ? null : mapping.apply(fromValue);
    if (mappedValue != null) {
      bind(value, mappedValue);
      return mappedValue.getValue();

    } else {
      bind(value);
      return null;
    }
  }
}
