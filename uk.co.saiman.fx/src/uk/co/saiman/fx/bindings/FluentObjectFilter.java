/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.function.Predicate;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

public class FluentObjectFilter<T> extends FluentObjectBinding<T> {
  private final ObservableValue<T> value;
  private final Predicate<T> filter;

  public FluentObjectFilter(ObservableValue<T> value, Predicate<T> filter) {
    this.value = requireNonNull(value);
    this.filter = requireNonNull(filter);
    bind(value);
  }

  @Override
  protected T computeValue() {
    T fromValue = value.getValue();
    return (fromValue == null || !filter.test(fromValue)) ? null : fromValue;
  }
}
