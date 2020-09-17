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

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public abstract class FluentObjectBinding<T> extends ObjectBinding<T> {
  public static <T> FluentObjectBinding<T> over(ObservableValue<T> value) {
    return new FluentObjectBinding<T>() {
      {
        bind(value);
      }

      @Override
      protected T computeValue() {
        return value.getValue();
      }
    };
  }

  public FluentObjectBinding<T> orDefault(T defaultValue) {
    return or(new SimpleObjectProperty<>(defaultValue));
  }

  public FluentObjectBinding<T> or(ObservableValue<T> alternative) {
    return new FluentObjectOr<>(this, alternative);
  }

  public <U> FluentObjectBinding<U> map(Function<T, U> mapping) {
    return new FluentObjectMap<>(this, mapping);
  }

  public <U> FluentObjectBinding<U> flatMap(Function<T, ObservableValue<U>> mapping) {
    return new FluentObjectFlatMap<>(this, mapping);
  }

  public FluentDoubleBinding mapToDouble(Function<T, Double> mapping) {
    return new FluentObjectMapToDouble<>(this, mapping);
  }

  public FluentObjectBinding<T> filter(Predicate<T> filter) {
    return new FluentObjectFilter<>(this, filter);
  }

  public FluentObjectBinding<T> withDependency(ObservableValue<?>... o) {
    return new FluentObjectDependent<>(this, o);
  }

  public FluentObjectBinding<T> withNullableDependency(Observable o) {
    throw new UnsupportedOperationException();
  }
}
